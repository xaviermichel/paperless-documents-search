angular.module('edmApp')
	.controller('DocumentSearchController', 
			['$scope', '$http', '$location', '$routeParams', '$sce', 
	 function($scope,   $http,   $location,   $routeParams,   $sce) {

	$scope.searchedPattern = $routeParams.q || "";

	$scope.searchSubmit = function() {
		if ($scope.searchedPattern.trim().length === 0) {
			return;
		}
		console.debug("Search : " + $scope.searchedPattern);
		$location.path('/').search({
			'q' : $scope.searchedPattern
		});
	}
	
	$scope.getTrustedHtmlContent = function(htmlString) {
		return $sce.trustAsHtml(htmlString);
	}
	
	$scope.linkToDocument = function(documentLink){
		if (documentLink.indexOf("http") === 0) {
			return documentLink;
		}
		return "file:///" + documentLink;
	}
	
	$http.get('/document?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		$scope.searchResults = response;
	});
}]);
