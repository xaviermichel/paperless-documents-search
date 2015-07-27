var gulp = require('gulp'),
    prettify = require('gulp-jsbeautifier'),
    runSequence = require('run-sequence'),
    gutil = require('gulp-util'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    rename = require("gulp-rename"),
    minifyCss = require('gulp-minify-css'),
    karma = require('gulp-karma'),
    argv = require('yargs').argv;


var EDM_WEBAPP_DIR = 'src/main/resources/';

var paths = {
    scripts: [
        EDM_WEBAPP_DIR + 'static/js/*.js',
        'gulpfile.js',
        'scripts/**/*.js'
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

gulp.task('concat-js', function() {
    return gulp.src(paths.minify)
        .pipe(concat('production.js'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/js'));
});

gulp.task('uglify-js', function() {
    return gulp.src([EDM_WEBAPP_DIR + 'static/build/js/*.js'])
        .pipe(uglify())
        .pipe(rename('production.min.js'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/js'));
});

gulp.task('minify-js', ['concat-js', 'uglify-js']);

gulp.task('minify-css', function() {
    return gulp.src(paths.style)
        .pipe(minifyCss())
        .pipe(rename('production.min.css'))
        .pipe(gulp.dest(EDM_WEBAPP_DIR + 'static/build/css'));
});

gulp.task('verify-js', function() {
    gulp.src(paths.scripts)
        .pipe(prettify({
            config: '.jsbeautifyrc',
            mode: 'VERIFY_ONLY'
        }));
});

gulp.task('prettify-js', function() {
    gulp.src(paths.scripts)
        .pipe(prettify({
            config: '.jsbeautifyrc',
            mode: 'VERIFY_AND_WRITE'
        }))
        .pipe(gulp.dest(function(file) {
            return file.base;
        }));
});

gulp.task('prettify-html', function() {
    gulp.src(paths.templates)
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

gulp.task('karma', skipTests(function() {
    return gulp
        .src(paths.unitTestFiles)
        .pipe(karma({
            configFile: 'karma.conf.js',
            action: 'run'
        }))
        .on('error', function(err) {
            process.stdout.write(err);
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
}));

gulp.task('default', ['prettify-code']);
