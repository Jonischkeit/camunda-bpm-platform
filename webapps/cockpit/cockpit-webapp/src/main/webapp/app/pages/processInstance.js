"use strict";

define(["angular"], function(angular, BpmnRender) {

  var module = angular.module("cockpit.pages");

  var Controller = function($scope, $routeParams, ProcessInstanceResource) {

    $scope.processDefinitionId = $routeParams.processDefinitionId;
    $scope.processInstanceId = $routeParams.processInstanceId;
    
    ProcessInstanceResource.activityInstances(
      {
        id: $scope.processInstanceId
      })
      .$then(function(result) {
        $scope.activityInstances = result.data;
      });

  };

  Controller.$inject = [ '$scope', '$routeParams', 'ProcessInstanceResource' ];

  var RouteConfig = function ($routeProvider) {
    $routeProvider.when('/process-definition/:processDefinitionId/process-instance/:processInstanceId', {
      templateUrl: 'pages/process-instance.html',
      controller: Controller,
      reloadOnSearch: false
    });
  };

  RouteConfig.$inject = ['$routeProvider'];

  module
    .config(RouteConfig);

});