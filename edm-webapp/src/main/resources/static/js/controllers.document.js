angular.module('edmApp')
	.controller('DocumentSearchController', 
			['$scope', '$http', '$location', '$routeParams', '$sce', 
	 function($scope,   $http,   $location,   $routeParams,   $sce) {

	$scope.searchedPattern = $routeParams.q || "";

	$scope.autocompleteDocumentList = [];
	
	$scope.topKeywordsToSee = [];
	
	$scope.aggregations = {};
	$scope.fileExtension = [];
	$scope.date = [];

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
		var requestPrefix = "";
		if ( $scope.searchedPattern.trim().length !== 0) {
			requestPrefix = $scope.searchedPattern.trim() + ' AND ';
		}
		$scope.searchedPattern = requestPrefix + pattern;
		$scope.searchSubmit();
	}
	
	$scope.searchPatternHaveBeenUpdated = function() {
		$http.get('/document/suggest/?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
			$scope.autocompleteDocumentList = response;
		});
	}
	
	$scope.pad = function(num, size) {
	    var s = num+"";
	    while (s.length < size) s = "0" + s;
	    return s;
	}
	
	$scope.searchAggregationsHaveBeenUpdated = function() {
		if ($scope.searchedPattern.trim().length === 0) {
			return;
		}
		console.log("aggregation updated");
			
		// extensions parsing...
		console.debug("Extensions fileExtension models :");
		console.debug($scope.fileExtension.aggValues);
		var extensionsFilterItems = [];
		for (var property in $scope.fileExtension.aggValues) {
	    	if ($scope.fileExtension.aggValues[property]) { // is checked !
	    		extensionsFilterItems.push(property);
	    	}
		}
		console.log("aggregation fileExtension items :");
		console.debug(extensionsFilterItems);
		var extensionsFilter = "";
		if (extensionsFilterItems.length > 0) {
			extensionsFilter = "fileExtension:" + extensionsFilterItems.join(" OR fileExtension:");
			extensionsFilter = " AND (" + extensionsFilter + ")";
		}
		console.debug("Extension fileExtension filter : ");
		console.debug(extensionsFilter);
		
		// date parsing...
		console.debug("Extensions date models :");
		console.debug($scope.date.aggValues);
		var dateFilterItems = [];
		for (var property in $scope.date.aggValues) {
	    	if ($scope.date.aggValues[property]) { // is checked !
	    		var from = moment(property).startOf("month").format('YYYY-MM-DD');
	    		var to   = moment(property).endOf("month").format('YYYY-MM-DD');
	    		dateFilterItems.push("[" + from + " TO " + to + "]");
	    	}
		}
		console.log("aggregation date items :");
		console.debug(dateFilterItems);
		var dateFilter = "";
		if (dateFilterItems.length > 0) {
			dateFilter = "date:" + dateFilterItems.join(" OR date:");
			dateFilter = " AND (" + dateFilter + ")";
		}
		console.debug("Extension date filter : ");
		console.debug(dateFilter);
		
		
		// submit request with all filters
		$http.get('/document?q=' + $scope.searchedPattern + extensionsFilter + dateFilter).success(function(response, status, headers, config) {
			$scope.searchResults = response;
		});
	}
	
	$http.get('/document?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		if ($scope.searchedPattern.trim().length === 0) {
			return;
		}
		$scope.searchResults = response;
	});
	
	$http.get('/document/top_terms?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		$scope.topKeywordsToSee = response;
	});
	
	$http.get('/document/aggregations?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
		$scope.aggregations = response;
	});
	
}]);
