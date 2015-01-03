// see http://la-cascade.ghost.io/grunt-pour-ceux-qui-pensent-que-grunt-est-compliqu%C3%A9/

var EDM_WEBAPP_DIR = 'edm-webapp/src/main/resources/static/';

module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),

		concat: {
			dist: {
				src: [
					// project resources
					EDM_WEBAPP_DIR + '/js/*.js',
					// external resources without CDN
					EDM_WEBAPP_DIR + '/bower_inc/angular-chart.js/dist/angular-chart.js'
				],
				dest: EDM_WEBAPP_DIR + '/build/js/production.js'
			}
		},
		uglify: {
			build: {
				src: EDM_WEBAPP_DIR + '/build/js/production.js',
				dest: EDM_WEBAPP_DIR + '/build/js/production.min.js'
			}
		},
		cssmin: {
			dist: {
				src: [
					// project resources
					EDM_WEBAPP_DIR + '/css/*.css',
					// external resources without CDN
					EDM_WEBAPP_DIR + '/bower_inc/angular-chart.js/dist/angular-chart.css'
				],
				dest: EDM_WEBAPP_DIR + '/build/css/production.min.css'
			}
		},
	});

	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-cssmin');

	grunt.registerTask('default', ['concat', 'uglify', 'cssmin']);
};
