angular.module('edmApp')
	.controller('DocumentSearchController', 
			['$scope', '$http', '$location', '$routeParams', '$sce', 
	 function($scope,   $http,   $location,   $routeParams,   $sce) {

	$scope.searchedPattern = $routeParams.q;

	$scope.searchSubmit = function() {
		console.debug("Search : " + $scope.searchedPattern);
		$location.path('/').search({
			'q' : $scope.searchedPattern
		});
	}
	
	$scope.getTrustedHtmlContent = function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
	
	$http.get('/document?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		$scope.searchResults = response;
	});
}]);
