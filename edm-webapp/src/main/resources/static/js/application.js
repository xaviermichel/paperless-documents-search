angular.module('edmApp', ['ngRoute', 'nodeService', 'documentService', 'notificationService', 'momentFilter']).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.
    when('/', // /?q=bazinga
        {
            templateUrl: 'resources/views/document-search-result.html',
            controller: 'DocumentSearchController'
        }
    ).
    otherwise({
        redirectTo: '/404.html'
    });
}]);
