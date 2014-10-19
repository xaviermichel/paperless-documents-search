angular.module('edmApp')
	.controller('DocumentSearchController', 
			['$scope', '$http', '$location', '$routeParams', '$sce', 
	 function($scope,   $http,   $location,   $routeParams,   $sce) {

	$scope.searchedPattern = $routeParams.q || "";

	$scope.autocompleteDocumentList = [];
	
	$scope.topKeywordsToSee = [];
	
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
	
	$scope.getDocumentNodeIcon = function(node) {
		switch(node.fileExtension.toLowerCase()) {
		    case "pdf":
		        return "pdf";
		        break;
		    case "html":
		        return "html";
		        break;
		    case "png":
		    case "jpg":
		    case "jpeg":
		    case "gif":
		        return "image";
		        break;
		    case "doc":
		    case "docx":
		        return "word";
		        break;
		    case "xls":
		    case "xlsx":
		        return "excel";
		        break;
		    case "ppt":
		    case "pptx":
		        return "power-point";
		        break;
		    case "txt":
		    	return "text";
		    	break;
		}
		return "unknown"; // default icon
	}
	
	$scope.updateSearchPattern = function(pattern) {
		$scope.searchedPattern = pattern;
		$scope.searchSubmit();
	}
	
	$scope.searchPatternHasBeenUpdated = function() {
		$http.get('/document/suggest/?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
			$scope.autocompleteDocumentList = response;
		});
	}
	
	$http.get('/document?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		if ($scope.searchedPattern.trim().length === 0) {
			return;
		}
		$scope.searchResults = response;
	});
	
	$http.get('/document/top_terms').success(function(response, status, headers, config) {
		$scope.topKeywordsToSee = response;
	});
	
}]);
