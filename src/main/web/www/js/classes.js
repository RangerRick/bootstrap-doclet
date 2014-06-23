(function() {
	'use strict';

	angular.module('javadoc.classes', [])

	.controller('ClassesCtrl', ['$log', '$scope', 'PackageService', function($log, $scope, PackageService) {
		PackageService.getPackages().then(function(packages) {
			var classes = [], pack;
			for (var i=0; i < packages.length; i++) {
				pack = packages[i];
				for (var j=0; j < pack.classes.length; j++) {
					classes.push({
						name: pack.classes[j].name,
						package: pack.name
					});
				}
			}
			$scope.classes = classes;
		});

		$log.info('ClassesCtrl initialized.');
	}])

	.controller('ClassCtrl', ['$log', '$scope', '$stateParams', 'PackageService', function($log, $scope, $stateParams, PackageService) {
		$scope.from = $stateParams.from;
		$scope.packageName = $stateParams.packageName;
		$scope.className = $stateParams.className;

		$log.info('ClassCtrl initialized.');
	}])

	;
}());