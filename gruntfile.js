// see http://la-cascade.ghost.io/grunt-pour-ceux-qui-pensent-que-grunt-est-compliqu%C3%A9/

var EDM_WEBAPP_DIR = 'edm-webapp/src/main/webapp';

module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),

		concat: {
			dist: {
				src: [
					// not uglified resources
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-canvas-to-blob/js/canvas-to-blob.min.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/vendor/jquery.ui.widget.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.iframe-transport.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-process.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-image.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-audio.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-video.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-validate.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/js/jquery.fileupload-angular.js',
					EDM_WEBAPP_DIR + '/resources/bower_inc/angular-kendo/build/angular-kendo.min.js',
					// project resources
					EDM_WEBAPP_DIR + '/resources/js/*.js'
				],
				dest: EDM_WEBAPP_DIR + '/resources/build/js/production.js'
			}
		},
		uglify: {
			build: {
				src: EDM_WEBAPP_DIR + '/resources/build/js/production.js',
				dest: EDM_WEBAPP_DIR + '/resources/build/js/production.min.js'
			}
		},
		cssmin: {
			dist: {
				src: [
					// not minimified resources
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/css/jquery.fileupload.css',
					EDM_WEBAPP_DIR + '/resources/bower_inc/blueimp-file-upload/css/jquery.fileupload-ui.css',
					// project resources
					EDM_WEBAPP_DIR + '/resources/css/*.css'
				],
				dest: EDM_WEBAPP_DIR + '/resources/build/css/production.min.css'
			}
		},
	});

	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-cssmin');

	grunt.registerTask('default', ['concat', 'uglify', 'cssmin']);
};
