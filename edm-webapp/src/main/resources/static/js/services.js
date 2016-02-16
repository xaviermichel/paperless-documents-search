angular.module('categoryService', ['ngResource']).factory('Category', ['$resource', function($resource) {
    return $resource('category/:id', {}, {});
}]);

// filter for date formatting with moment js
angular.module('momentFilter', []).filter('moment', function() {
    return function(input, format) {
        format = format || 'll';
        return moment(input).format(format);
    };
});

// see also https://github.com/tchatel/angular-notifications
// just added a level support and remove undo
// notification.html is required
angular.module('notificationService', [])
    .controller('NotificationsCtrl', ['$scope', 'notification', function($scope, notification) {
        $scope.notifications = notification.list;
    }])
    .factory('notification', ['$timeout', function($timeout) {
        var service = {
            list: {},
            levelsStyle: {
                'ERROR': 'alert-error',
                'WARN': '',
                'INFO': 'alert-success',
                'LOG': 'alert-info'
            },
            add: function(level, text, delay) {
                var timestamp = (new Date()).getTime();
                service.list[timestamp] = {
                    text: text,
                    style: service.levelsStyle[level]
                };
                $timeout(function() {
                    delete service.list[timestamp];
                }, (delay || 5) * 1000);
            }
        };
        return service;
    }]);
