// see http://la-cascade.ghost.io/grunt-pour-ceux-qui-pensent-que-grunt-est-compliqu%C3%A9/

var EDM_WEBAPP_DIR = 'edm-webapp/src/main/webapp';

module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),

		concat: {
			dist: {
				src: [
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
