var gulp = require('gulp'),
	connect = require('gulp-connect'),
	less = require('gulp-less'),
	bower = require('gulp-bower');

gulp.task('bower', function() {
	return bower();
	/* .pipe(gulp.dest('lib/')); */
});

gulp.task('webserver', function() {
	connect.server({
		root: 'www',
		livereload: true
	});
});

gulp.task('less', function() {
	gulp.src('www/styles/styles.less')
		.pipe(less())
		.pipe(gulp.dest('www/styles'))
		.pipe(connect.reload());
});

gulp.task('html', function() {
	gulp.src('www/**/*.html').pipe(connect.reload());
});

gulp.task('js', function() {
	gulp.src('www/js/**/*.js').pipe(connect.reload());
});

gulp.task('watch', function() {
	gulp.watch('www/**/*.html', ['html']);
	gulp.watch('www/js/**/*.js', ['js']);
	gulp.watch('www/styles/**/*.less', ['less']);
	gulp.watch('www/data/**/*.json', ['js']);
});

gulp.task('default', ['less', 'webserver', 'watch']);
gulp.task('maven', ['less', 'bower']);
