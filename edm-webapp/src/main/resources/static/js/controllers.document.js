angular.module('edmApp')
    .controller('DocumentSearchController', ['$scope', '$http', '$location', '$routeParams', '$sce', '$route', 'Category',
        function($scope, $http, $location, $routeParams, $sce, $route, Category) {

            $scope.searchedPattern = $routeParams.q || "";

            $scope.categories = Category.query();
            $scope.autocompleteDocumentList = [];
            $scope.topTerms = [];
            $scope.aggregations = {
                fileDate: [],
                fileExtension: []
            };

            // prevent re-loading when searching
            var lastRoute = $route.current;
            $scope.$on('$locationChangeSuccess', function(event) {
                if ($route.current.params.q) {
                    $route.current = lastRoute;
                }
            });

            $scope.findCategoryById = function(categoryId) {
                return $scope.categories.filter(function(category) {
                    return category.id === categoryId;
                })[0];
            };

            $scope.getTrustedHtmlContent = function(htmlString) {
                return $sce.trustAsHtml(htmlString);
            };

            $scope.linkToDocument = function(edmDocument) {
                // web link
                if (edmDocument.nodePath.indexOf("http") === 0) {
                    return edmDocument.nodePath;
                }
                // network link
                if (edmDocument.nodePath.indexOf("//") === 0) {
                    return edmDocument.nodePath.replace(/\//g, "\\"); // windows style
                }
                // local file
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

            $scope.extractAggregationInfoFromDateAgg = function(key) {
                computeValue = function(monthToRemove) {
                    var fromDate = moment().subtract(monthToRemove, 'months').startOf("month").format('YYYY-MM-DD');
                    var toDate = moment().endOf("month").format('YYYY-MM-DD'); // can be removed for '*' ?
                    return "(date:[" + fromDate + " TO " + toDate + "])";
                };

                var candidates = {
                    last_year: {
                        label: "Durant l'année passée",
                        value: computeValue(12)
                    },
                    last_6_months: {
                        label: "Durant ces 6 derniers mois",
                        value: computeValue(6)
                    },
                    last_2_months: {
                        label: "Durant ces 2 derniers mois",
                        value: computeValue(2)
                    },
                    last_month: {
                        label: "Durant le mois qui vient de s'écouler",
                        value: computeValue(1)
                    }
                };

                return candidates[key];
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
                        return e.isChecked && e.key;
                    })
                    .map(function formatedQuery(e) {
                        return "fileExtension:" + e.key;
                    })
                    .join(" OR ");
                if (aggregateFileExtensionFilter) {
                    aggregateFileExtensionFilter = " AND (" + aggregateFileExtensionFilter + ")";
                }
                console.debug("aggregateFileExtensionFilter = " + aggregateFileExtensionFilter);

                // date
                var aggregateDateFilter = "";
                if ($scope.dateAggregationFilter) {
                    aggregateDateFilter = " AND " + $scope.dateAggregationFilter;
                }
                console.debug("aggregateDateFilter = " + aggregateDateFilter);

                // category
                var categoryFilter = $scope.categories
                    .filter(function isChecked(e) {
                        return e.isChecked;
                    })
                    .map(function formatedQuery(e) {
                        return "categoryId:" + e.id;
                    })
                    .join(" OR ");
                if (categoryFilter) {
                    categoryFilter = " AND (" + categoryFilter + ")";
                }
                console.debug("categoryFilter = " + categoryFilter);

                // final filter
                var finalFilter = aggregateFileExtensionFilter + aggregateDateFilter + categoryFilter;
                console.info("final filter : " + finalFilter);
                return finalFilter;
            };

            $scope.submitSearchWithFilters = function() {
                var filters = $scope._getQueryFilters();

                // submit request with all filters
                $http.get('/document?q=' + $scope.searchedPattern + filters).success(function(response, status, headers, config) {
                    $scope.searchResults = response;
                });
            };

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
                });
            };

            // initialization
            $scope._sendSearchRequest();
        }
    ]);
