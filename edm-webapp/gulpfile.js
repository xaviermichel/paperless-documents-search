var gulp = require('gulp'),
    prettify = require('gulp-jsbeautifier'),
    runSequence = require('run-sequence'),
    gutil = require('gulp-util'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    rename = require("gulp-rename"),
    wait = require('gulp-wait'),
    minifyCss = require('gulp-minify-css'),
    karma = require('gulp-karma'),
    argv = require('yargs').argv,
    shell = require('gulp-shell'),
    protractor = require('gulp-protractor').protractor;


var EDM_WEBAPP_DIR = 'src/main/resources/';
var EDM_TEST_DIR = 'src/test/resources/';

var serverPort = 9053;

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
        'src/main/resources/static/bower_inc/jquery/jquery.min.js',
        'src/main/resources/static/bower_inc/angular/angular.min.js',
        'src/main/resources/static/bower_inc/angular-route/angular-route.min.js',
        'src/main/resources/static/bower_inc/angular-resource/angular-resource.min.js',
        'src/main/resources/static/bower_inc/angular-animate/angular-animate.min.js',
        'src/main/resources/static/bower_inc/moment/min/moment.min.js',
        'src/main/resources/static/bower_inc/angular-mocks/angular-mocks.js',
        'src/main/resources/static/js/*.js',
        'src/test/resources/static/js/unit/*.spec.js'
    ],
    e2eTestFiles: [
        'src/test/resources/static/js/e2e/*.js'
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

gulp.task('prettify-code', function() {
    runSequence(
        ['prettify-js', 'prettify-html']
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
//     mvn spring-boot:run -D"run.arguments=--server.port=9053"
gulp.task('backend-server', onlyOnTravis(function() {
    shell.task([
        'mvn spring-boot:run -D"run.arguments=--server.port=' + serverPort + '"'
    ]);
}));

gulp.task('protractor', skipTests(function() {
    gulp.start('backend-server');

    return gulp.src(['src/test/resources/static/js/e2e/*.spec.js'])
        .pipe(protractor({
            configFile: 'protractor.conf.js',
            args: ['--baseUrl', 'http://localhost:' + serverPort]
        }))
        .on('error', function(e) {
            process.stdout.write('Protractors test has failed !\nIf you are using a local environment, have you launched webdriver and application ?\n');
            throw e;
        });
}));

gulp.task('protractor-only-on-travis', onlyOnTravis(function() {
    runSequence(
        ['protractor']
    );
}));

// if you use a local env, you may want to coment protractor test because they are really slow !
gulp.task('test', function() {
    runSequence('karma' /*, 'protractor-only-on-travis'*/ );
});

gulp.task('default', ['prettify-code']);
