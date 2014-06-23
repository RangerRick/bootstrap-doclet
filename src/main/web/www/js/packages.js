(function() {
	'use strict';

	angular.module('javadoc.packages', [])

	.factory('PackageService', ['$q', '$log', '$timeout', '$http', function($q, $log, $timeout, $http) {
		var _packages = __packages;

		var assertPackagesLoaded = function() {
			var deferred = $q.defer();

			if (_packages === undefined) {
				$http.get('data/packages.json').success(function(data, status) {
					_packages = data;
					deferred.resolve(data);
				}).error(function(data, status) {
					$log.debug('Failed to get packages.json', data, status);
					deferred.reject(data + '/' + status);
				});
			} else {
				$timeout(function() {
					deferred.resolve(_packages);
				});
			}

			return deferred.promise;
		};

		var getPackages = function() {
			var deferred = $q.defer();

			assertPackagesLoaded().then(function(packages) {
				deferred.resolve(packages);
			}, function(err) {
				deferred.reject(err);
			});

			return deferred.promise;
		};

		var getPackage = function(packageName) {
			var deferred = $q.defer();

			assertPackagesLoaded().then(function(packages) {
				for (var i=0; i < packages.length; i++) {
					var pack = packages[i];
					if (pack.name === packageName) {
						deferred.resolve(pack);
					}
				}
				deferred.resolve(undefined);
			}, function(err) {
				deferred.reject(err);
			});

			return deferred.promise;
		};

		$log.info('PackageService initialized.');

		return {
			getPackages: getPackages,
			getPackage: getPackage
		};
	}])

	.controller('PackagesCtrl', ['$log', '$scope', 'PackageService', function($log, $scope, PackageService) {
		PackageService.getPackages().then(function(packages) {
			$scope.packages = packages;
		});

		$log.info('PackagesCtrl initialized.');
	}])

	.controller('PackageCtrl', ['$log', '$scope', '$stateParams', 'PackageService', function($log, $scope, $stateParams, PackageService) {
		var packageName = $stateParams.packageName;

		PackageService.getPackage(packageName).then(function(pack) {
			$scope.package = pack;
		});

		$log.info('PackageCtrl initialized. Package = ' + packageName);
	}])

	;
}());