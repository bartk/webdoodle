'use strict';

function CanvasListController($scope, $resource, $timeout) {
    $scope.canvases = [];
    $scope.contextPath;

    var pollingTask;

    $scope.init = function() {
        //start polling
        $resource('api/config').get(function(config) {
            $scope.contextPath = config.contextPath;
            $scope.poll();
        });
    };

    $scope.$on('$destroy', function() {
        //stop polling
        $timeout.cancel(pollingTask);
    });

    $scope.all = function() {
        $resource('api/canvas').query(function(canvases) {
            if (!angular.equals($scope.canvases, canvases)) {
                $scope.canvases = canvases;
            }
        }, function() {
            console.log("fail");
        });
    };

    $scope.poll = function() {
        $scope.all();
        pollingTask = $timeout($scope.poll, 1000);
    };

    $scope.create = function() {
        $resource('api/canvas').save({name: $scope.name, width: $scope.width, height: $scope.height, closed: false});

        $scope.name = null;
        $scope.width = null;
        $scope.height = null;
    };

    $scope.close = function(canvasId) {
        $scope.canvases.forEach(function(c) {
            if (c.id == canvasId) {
                $resource('api/canvas/:id', {id: '@id'}).get({id: canvasId}, function(canvas) {
                    c.closed = true;
                    canvas.$delete();
                });
            }
        });
    };
}
;

function CanvasEditorController($scope, $resource, $routeParams) {
    var canvasElement = document.getElementById('workingCanvas');
    var canvasContext = canvasElement.getContext('2d');
    var ws;

    $scope.selectedColor = '#000000';

    $scope.init = function() {
        canvasElement.onselectstart = function() {
            return false;
        };

        getPixelsAndPaint($routeParams.id);
        
        $resource('api/canvas/palette').get(function(palette) {
            if (!angular.equals($scope.palette, palette)) {
                $scope.palette = palette;
            }
        });

        $resource('api/canvas/:id', {id: '@id'}).get({id: $routeParams.id}, function(canvas) {
            $scope.canvas = canvas;
            $resource('api/config').get(function(config) {
                openWebSocket(getWebSocketUrl(config.contextPath), canvas.id);
            });
        });
    };

    var getWebSocketUrl = function(contextPath) {
        var hostPattern = /[A-Za-z]+:\/\/([^\/?#]+)(?:[\/?#]|$)/;
        var hostName = document.URL.match(hostPattern)[1];
        return 'ws://' + hostName + contextPath;
    };

    var openWebSocket = function(webSocketUrl, canvasId) {
        ws = new WebSocket(webSocketUrl + '/canvas/' + canvasId);
        window.onbeforeunload = function() {
            ws.onclose = function() {
            }; // disable onclose handler first
            ws.close();
        };

        ws.onmessage = function(event) {
            var pixel = JSON.parse(event.data);
            paint(pixel.x, pixel.y, pixel.color);
        };
    };

    var getPixelsAndPaint = function(canvasId) {
        $resource('api/canvas/:id/pixels', {id: '@id'}).query({id: canvasId}, function(pixels) {
            pixels.forEach(function(p) {
                paint(p.x, p.y, p.color);
            });
        });
    };

    var paint = function(x, y, color) {
        canvasContext.beginPath();
        canvasContext.strokeStyle = color;
        canvasContext.arc(x, y, 2, 0, 2 * Math.PI, false);
        canvasContext.fillStyle = color;
        canvasContext.fill();
        canvasContext.stroke();
    };

    var sendPixel = function(x, y, color) {
        ws.send(JSON.stringify({"canvasId": $scope.canvas.id, "x": x, "y": y, "color": color}));
    };

    var pencildown;

    $scope.mousemove = function($event) {
        if (pencildown) {
            var rect = canvasElement.getBoundingClientRect();
            paint($event.clientX - rect.left, $event.clientY - rect.top, $scope.selectedColor);
            sendPixel($event.clientX - rect.left, $event.clientY - rect.top, $scope.selectedColor);
        }
    };

    $scope.mouseup = function($event) {
        if ($event.button == 2) {
            return false;
        }
        pencildown = false;
    };

    $scope.mousedown = function($event) {
        if ($event.button == 2) {
            return false;
        }

        pencildown = true;
    };
    
    $scope.selectColor = function(color) {
        $scope.selectedColor = color;
    }
}
;

function CanvasBrowserController($scope, $resource, $routeParams) {
    $scope.init = function() {
        $resource('api/config').get(function(config) {
            $scope.contextPath = config.contextPath;
            $resource('api/canvas/:id', {id: '@id'}).get({id: $routeParams.id}, function(canvas) {
                $scope.canvas = canvas;
            });
        });

    };


}
;
