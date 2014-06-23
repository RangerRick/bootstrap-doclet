(function() {
	'use strict';

	angular.module('javadoc',
	[
		'ng',
		'ui.router',
		'javadoc.classes',
		'javadoc.packages'
	])

	.controller('MenuCtrl', ['$log', '$state', '$scope', function($log, $state, $scope) {
		$scope.$on('$stateChangeSuccess', function(ev, state, parms) {
			$scope.state = state;
			$scope.parms = parms;
		});

		$scope.isActive = function(name) {
			if ($state.includes(name)) {
				return true;
			}
			if ($scope.parms && $scope.parms.from && $scope.parms.from === name) {
				return true;
			}
			return false;
		};

		$log.info('MenuCtrl initialized.');
	}])

	.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
		$stateProvider
			.state('packages', {
				url: '/packages',
				templateUrl: 'templates/packages.html',
				controller: 'PackagesCtrl'
			})
			.state('class', {
				url: '/:from/class-detail/:packageName/:className',
				templateUrl: 'templates/class-summary.html',
				controller: 'ClassCtrl'
			})
			.state('packages.package', {
				url: '/package/:packageName',
				templateUrl: 'templates/package-summary.html',
				controller: 'PackageCtrl'
			})
			.state('classes', {
				url: '/classes',
				templateUrl: 'templates/classes.html',
				controller: 'ClassesCtrl'
			})
		;

		$urlRouterProvider.otherwise('/packages');
	}])

	;
}());