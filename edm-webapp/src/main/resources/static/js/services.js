angular.module('nodeService', [ 'ngResource' ]).factory('Node', function($resource) {
	return $resource('node/:id', {}, {});
});

angular.module('libraryService', [ 'ngResource' ]).factory('Library', function($resource) {
	return $resource('library/:id', {}, {});
});

angular.module('directoryService', [ 'ngResource' ]).factory('Directory', function($resource) {
	return $resource('directory/:id', {}, {});
});

angular.module('documentService', [ 'ngResource' ]).factory('Document', function($resource) {
	return $resource('document/:id', {}, {});
});

// see also https://github.com/tchatel/angular-notifications
// just added a level support and remove undo
// notification.html is required
angular.module('notificationService', [])
.controller('NotificationsCtrl', ['$scope', 'notification', function ($scope, notification) {
    $scope.notifications = notification.list;
}])
.factory('notification', ['$timeout', function ($timeout) {
    var service = {
        list: {},
        levelsStyle : {
    		'ERROR' : 'alert-error',
    		'WARN' : '',
    		'INFO' : 'alert-success',
    		'LOG' : 'alert-info'
        },
        add: function (level, text, delay) {
            var timestamp = (new Date()).getTime();
            service.list[timestamp] = {
                text: text,
                style: service.levelsStyle[level]
            };
            $timeout(function () {
                delete service.list[timestamp];
            }, (delay || 5) * 1000);
        }
    };
    return service;
}]);
    