"use strict";

angular
  .module('latcraftContestApp')
  .config(['$routeProvider', function($routeProvider) {

    $routeProvider.when('/results', {
      templateUrl: 'views/results/results.html',
      controller: 'ResultController'
    });

  }])
  .controller('ResultController', function($scope, $log, $http, $cookies, $interval, $route, $routeParams) {

    $scope.resultCtrl = this;
    this.lastResults = [];
    this.topResults = [];

    this.loadResults = function() {
      $http.get('/api/v1/result/last').success(function(data, status, headers, config) {
        $log.debug('Received last result data:', data)
        if (data.response == 'OK') {
          $scope.resultCtrl.lastResults = data.results;
          $log.debug('Data is loaded!');
        }
      });
      $http.get('/api/v1/result/top').success(function(data, status, headers, config) {
        $log.debug('Received top result data:', data)
        if (data.response == 'OK') {
          $scope.resultCtrl.topResults = data.results;
          $log.debug('Data is loaded!');
        }
      });
    };

    this.loadResults();

    $interval(function() {
      $scope.resultCtrl.loadResults();
    }, 10000);

    $scope.$on('loadResults', function(event, args) {
      $log.debug('Received data loading event!');
      $scope.resultCtrl.loadResults();
    });

  });

