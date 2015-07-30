angular.module('edmApp')
    .controller('DocumentSearchController', ['$scope', '$http', '$location', '$routeParams', '$sce',
        function($scope, $http, $location, $routeParams, $sce) {

            $scope.searchedPattern = $routeParams.q || "";

            $scope.autocompleteDocumentList = [];
            $scope.topTerms = [];
            $scope.aggregations = {};

            $scope.searchSubmit = function() {
                if ($scope.searchedPattern.trim().length === 0) {
                    return;
                }
                console.debug("Search : " + $scope.searchedPattern);
                $location.path('/').search({
                    'q': $scope.searchedPattern
                });
            };

            $scope.getTrustedHtmlContent = function(htmlString) {
                return $sce.trustAsHtml(htmlString);
            };

            $scope.linkToDocument = function(edmDocument) {
                if (edmDocument.nodePath.indexOf("http") === 0) {
                    return edmDocument.nodePath;
                }
                return "/files?docId=" + edmDocument.id;
            };

            $scope.getDocumentNodeIcon = function(node) {
                switch (node.fileExtension.toLowerCase()) {
                    case "pdf":
                        return "pdf";
                    case "html":
                        return "html";
                    case "png":
                    case "jpg":
                    case "jpeg":
                    case "gif":
                        return "image";
                    case "doc":
                    case "docx":
                        return "word";
                    case "xls":
                    case "xlsx":
                        return "excel";
                    case "ppt":
                    case "pptx":
                        return "power-point";
                    case "txt":
                        return "text";
                }
                return "unknown"; // default icon
            };

            $scope.updateSearchPattern = function(pattern) {
                var requestPrefix = "";
                if ($scope.searchedPattern.trim().length !== 0) {
                    requestPrefix = $scope.searchedPattern.trim() + ' AND ';
                }
                $scope.searchedPattern = requestPrefix + pattern;
                $scope.searchSubmit();
            };

            $scope.searchPatternHaveBeenUpdated = function() {
                $http.get('/document/suggest/?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
                    $scope.autocompleteDocumentList = response;
                });
            };

            $scope.searchAggregationsHaveBeenUpdated = function() {
                if ($scope.searchedPattern.trim().length === 0) {
                    return;
                }
                console.log("aggregation updated");

                // file extension
                var aggregateFileExtensionFilter = $scope.aggregations.fileExtension
                    .filter(function isChecked(e) {
                        return e.isChecked;
                    })
                    .map(function formatedQuery(e) {
                        return "fileExtension:" + e.key;
                    })
                    .join(" OR ");

                if (aggregateFileExtensionFilter.length !== 0) {
                    aggregateFileExtensionFilter = " AND (" + aggregateFileExtensionFilter + ")";
                }
                console.debug("aggregateFileExtensionFilter = " + aggregateFileExtensionFilter);

                // date
                var aggregateDateFilter = $scope.aggregations.date
                    .filter(function isChecked(e) {
                        return e.isChecked;
                    })
                    .map(function formatedQuery(e) {
                        var from = moment(e.key).startOf("month").format('YYYY-MM-DD');
                        var to = moment(e.key).endOf("month").format('YYYY-MM-DD');
                        return "date:[" + from + " TO " + to + "]";
                    })
                    .join(" OR ");

                if (aggregateDateFilter.length !== 0) {
                    aggregateDateFilter = " AND (" + aggregateDateFilter + ")";
                }
                console.debug("aggregateDateFilter = " + aggregateDateFilter);


                // submit request with all filters
                $http.get('/document?q=' + $scope.searchedPattern + aggregateFileExtensionFilter + aggregateDateFilter).success(function(response, status, headers, config) {
                    $scope.searchResults = response;
                });
            };

            $scope.updateSearchPatternFromTopTerms = function(term) {
                $scope.updateSearchPattern(term);
                $scope.$apply();
            };

            $http.get('/document?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
                if ($scope.searchedPattern.trim().length === 0) {
                    return;
                }
                $scope.searchResults = response;
            });

            $http.get('/document/top_terms?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
                $scope.topTerms = response;
            });

            $http.get('/document/aggregations?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
                $scope.aggregations = response;
            });

        }
    ]);
