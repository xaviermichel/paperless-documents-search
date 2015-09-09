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
            $scope.submitSearch = function() {
                if ($scope.searchedPattern && $scope.searchedPattern.trim().length !== 0) {
                    $location.path('/').search({
                        'q': $scope.searchedPattern
                    });
                }
                console.debug("Search : " + $scope.searchedPattern);
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
                if ($scope.searchedPattern && $scope.searchedPattern.trim().length !== 0) {
                    requestPrefix = $scope.searchedPattern.trim() + ' AND ';
                }
                $scope.searchedPattern = requestPrefix + word;
                $scope.submitSearch();
            };

            /**
             * Automatically called when the search pattern is updated in front view
             */
            $scope.searchPatternHaveBeenUpdated = function() {
                $scope._refreshAutocompleteDocumentList();
                $scope.submitSearch();
            };

            /**
             * Get query filter as string
             * For example : " AND (date:[2015-09-01 TO 2015-09-30])"
             */
            $scope._getQueryFilters = function() {
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
                var aggregateDateFilter = "";
                if (!$("#fromDateFilter").val() || !$("#toDateFilter").val()) {
                    console.warn("fromDateFilter or/and toDateFilter is not defined, won't apply date filter");
                } else {
                    var from = moment($("#fromDateFilter").val()).startOf("month").format('YYYY-MM-DD');
                    var to = moment($("#toDateFilter").val()).endOf("month").format('YYYY-MM-DD');
                    aggregateDateFilter = " AND (date:[" + from + " TO " + to + "])";
                }
                console.debug("aggregateDateFilter = " + aggregateDateFilter);

                // final filter
                return aggregateFileExtensionFilter + aggregateDateFilter;
            };

            $scope.submitSearchWithFilters = function() {
                var filters = $scope._getQueryFilters();

                // submit request with all filters
                $http.get('/document?q=' + $scope.searchedPattern + filters).success(function(response, status, headers, config) {
                    $scope.searchResults = response;
                });
            }

            $scope._sendSearchRequest = function() {
                console.info("initilizing scope values (top terms, ...)");

                if ($scope.searchedPattern && $scope.searchedPattern.trim().length !== 0) {
                    $scope.submitSearchWithFilters();
                } else {
                    $scope.searchResults = null;
                }

                $http.get('/document/top_terms?q=' + ($scope.searchedPattern || '')).success(function(response, status, headers, config) {
                    $scope.topTerms = response;
                });

                $http.get('/document/aggregations?q=' + ($scope.searchedPattern || '')).success(function(response, status, headers, config) {
                    $scope.aggregations = response;
                    $scope.fromDate = ($scope.aggregations.date[0] ? new Date($scope.aggregations.date[0].key) : new Date());
                    $scope.toDate = ($scope.aggregations.date[1] ? new Date($scope.aggregations.date[1].key) : new Date());
                    $scope.fromDateFilter = new Date($scope.fromDate);
                    $scope.toDateFilter = new Date($scope.toDate);
                });
            };

            // initialization
            $scope._sendSearchRequest();

        }
    ]);
