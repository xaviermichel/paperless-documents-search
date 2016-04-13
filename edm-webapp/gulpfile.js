var gulp = require('gulp'),
    prettify = require('gulp-jsbeautifier'),
    runSequence = require('run-sequence'),
    gutil = require('gulp-util'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    rename = require("gulp-rename"),
    minifyCss = require('gulp-minify-css'),
    karma = require('gulp-karma'),
    argv = require('yargs').argv,
    shell = require('gulp-shell'),
    casperJs = require('gulp-casperjs');

var EDM_WEBAPP_DIR = 'src/main/resources/';
var EDM_TEST_DIR = 'src/test/resources/';
var EDM_BOWER_DIR = 'src/main/resources/static/bower_inc/';

var paths = {
    scripts: [
        EDM_WEBAPP_DIR + 'static/js/*.js',
        EDM_TEST_DIR + 'static/js/unit/*.js',
        EDM_TEST_DIR + 'static/js/e2e/*.js',
        '*.js',
        EDM_WEBAPP_DIR + 'mapping/*.json',
        EDM_WEBAPP_DIR + 'mapping/**/*.json'
    ],
    minify: [
        EDM_WEBAPP_DIR + 'static/js/*.js'
    ],
    style: [
        EDM_WEBAPP_DIR + 'static/css/*.css'
    ],
    templates: [
        EDM_WEBAPP_DIR + '/**/*.html',
        '!' + EDM_WEBAPP_DIR + 'static/bower_inc/**/*.html'
    ],
    unitTestFiles: [
        EDM_BOWER_DIR + 'jquery/jquery.min.js',
        EDM_BOWER_DIR + 'angular/angular.min.js',
        EDM_BOWER_DIR + 'angular-route/angular-route.min.js',
        EDM_BOWER_DIR + 'angular-resource/angular-resource.min.js',
        EDM_BOWER_DIR + 'angular-animate/angular-animate.min.js',
        EDM_BOWER_DIR + 'moment/min/moment.min.js',
        EDM_BOWER_DIR + 'angular-mocks/angular-mocks.js',
        EDM_WEBAPP_DIR + '/static/js/*.js',
        EDM_TEST_DIR + '/static/js/unit/*.spec.js'
    ],
    e2eTestFiles: [
        EDM_TEST_DIR + 'static/js/e2e/*.js',
        '!' + EDM_TEST_DIR + 'static/js/e2e/00-constants.js'
    ],
    e2eTestConstants: EDM_TEST_DIR + 'static/js/e2e/00-constants.js',
    poms: [
        '../**/pom.xml'
    ]
};

function skipTests(task) {
    if (argv.skipTests === 'true') {
        return function() {
            process.stdout.write('Skipped Tests...\n');
        };
    } else {
        return task;
    }
}

function onlyOnTravis(callback) {
    if (process.env.TRAVIS === 'true') {
        return callback;
    } else {
        return function() {
            process.stdout.write('Not on travis, won\'t run the given task !\n');
        };
    }
}

gulp.task('minify-js', function() {
    return gulp.src(paths.minify)
        .pipe(concat('production.js'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/js'))
        .pipe(uglify())
        .pipe(rename('production.min.js'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/js'));

});

gulp.task('minify-css', function() {
    return gulp.src(paths.style)
        .pipe(concat('production.css'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/css'))
        .pipe(minifyCss())
        .pipe(rename('production.min.css'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/css'));
});

gulp.task('verify-js', function() {
    return gulp.src(paths.scripts)
        .pipe(prettify({
            config: '.jsbeautifyrc',
            mode: 'VERIFY_ONLY'
        }));
});

gulp.task('prettify-js', function() {
    return gulp.src(paths.scripts)
        .pipe(prettify({
            config: '.jsbeautifyrc',
            mode: 'VERIFY_AND_WRITE'
        }))
        .pipe(gulp.dest(function(file) {
            return file.base;
        }));
});

gulp.task('prettify-html', function() {
    return gulp.src(paths.templates)
        .pipe(prettify({
            braceStyle: "collapse",
            indentChar: " ",
            indentSize: 4
        }))
        .pipe(gulp.dest(EDM_WEBAPP_DIR));
});

gulp.task('prettify-poms', function() {
    return gulp.src(paths.poms)
        .pipe(prettify({
            braceStyle: "collapse",
            indentChar: " ",
            indentSize: 4
        }))
        .pipe(gulp.dest(function(file) {
            return file.base;
        }));
});

gulp.task('prettify-code', function() {
    runSequence(
        ['prettify-js', 'prettify-html', 'prettify-poms']
    );
});

gulp.task('minify-code', function() {
    runSequence(
        ['minify-js', 'minify-css']
    );
});

gulp.task('watch', function() {
    gulp.watch(paths.minify, ['minify-code']);
});

gulp.task('karma', skipTests(function() {
    return gulp
        .src(paths.unitTestFiles)
        .pipe(karma({
            configFile: 'karma.conf.js',
            action: 'run'
        }))
        .on('error', function(err) {
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
}));

// only on travis, for local development, you should start spring-boot server yourself !
gulp.task('backend-server', shell.task([
    'docker run --name edm-backend-server -p 8053:8053 -v $(pwd)/src/test/resources/documents:/host_mount_point -d simple-data-search-webapp'
]));
gulp.task('wait-backend-server', shell.task([
    'LIMIT=60 ; counter=0 ; while [ $counter -lt $LIMIT -a -z "$(docker logs edm-backend-server | grep \'Started Application in\')" ]; do echo "[$counter] waiting for edm server..." && sleep 1 && counter=$((counter + 1)) ; done'
]));
gulp.task('stop-backend-server', shell.task([
    'docker rm -f edm-backend-server'
]));

gulp.task('test-casperjs', function() {
    return gulp.src(paths.e2eTestFiles)
        .pipe(casperJs({
            command: 'test --includes=' + paths.e2eTestConstants
        })) //run casperjs test
        .on('error', function(err) {
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
});

gulp.task('e2e-tests-only-on-travis', onlyOnTravis(function() {
    runSequence('backend-server', 'wait-backend-server', 'test-casperjs', 'stop-backend-server');
}));

// if you use a local env, you may want to coment protractor test because they are really slow !
gulp.task('test', function() {
    runSequence('karma', 'e2e-tests-only-on-travis');
});

gulp.task('default', ['prettify-code']);
