angular.module('edmApp')
    .controller('DocumentSearchController', ['$scope', '$http', '$location', '$routeParams', '$sce', '$route',
        function($scope, $http, $location, $routeParams, $sce, $route) {

            $scope.searchedPattern = $routeParams.q || "";

            $scope.autocompleteDocumentList = [];
            $scope.topTerms = [];
            $scope.aggregations = {};

            // prevent re-loading when searching
            var lastRoute = $route.current;
            $scope.$on('$locationChangeSuccess', function(event) {
                if ($route.current.params.q) {
                    $route.current = lastRoute;
                }
            });

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

            /**
             * Submit search, according to the content of `$scope.searchedPattern`
             */
            $scope._submitSearch = function() {
                if (!$scope.searchedPattern || $scope.searchedPattern.trim().length === 0) {
                    $scope._sendSearchRequest();
                    return;
                }
                console.debug("Search : " + $scope.searchedPattern);
                $location.path('/').search({
                    'q': $scope.searchedPattern
                });
                $scope._sendSearchRequest();
            };

            /**
             * Refresh auto-complete document list
             */
            $scope._refreshAutocompleteDocumentList = function() {
                $http.get('/document/suggest/?q=' + $scope.searchedPattern).success(function(response, status, headers, config) {
                    $scope.autocompleteDocumentList = response;
                });
            };

            /**
             * Add some word in the search word and submit search
             */
            $scope.addWordAndSubmitSearch = function(word) {
                var requestPrefix = "";
                if ($scope.searchedPattern.trim().length !== 0) {
                    requestPrefix = $scope.searchedPattern.trim() + ' AND ';
                }
                $scope.searchedPattern = requestPrefix + word;
                $scope._submitSearch();
            };

            /**
             * Automatically called when the search pattern is updated in front view
             */
            $scope.searchPatternHaveBeenUpdated = function() {
                $scope._refreshAutocompleteDocumentList();
                $scope._submitSearch();
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

            $scope._sendSearchRequest = function() {
                console.info("initilizing scope values (top terms, ...)");

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
            };

            // initialization
            $scope._sendSearchRequest();

        }
    ]);
