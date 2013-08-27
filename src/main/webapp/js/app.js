'use strict';

var webdoodleModule = angular.module('webdoodle', ['ng', 'ngResource']);

webdoodleModule.config(['$routeProvider', function($routePrivider) {
        $routePrivider.
                when('/canvasList', {templateUrl: 'partials/canvasList.html', controller: CanvasListController}).
                when('/editor/:id', {templateUrl: 'partials/canvasEditor.html', controller: CanvasEditorController}).
                when('/show/:id', {templateUrl: 'partials/canvasBrowser.html', controller: CanvasBrowserController}).
                otherwise({redirectTo: '/canvasList'});
}]);




