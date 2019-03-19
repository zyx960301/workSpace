app.controller('contentController',function($scope,contentService){

    $scope.addNum = function (x) {
        $scope.num=$scope.num+x;
        if($scope.num<1){
            $scope.num=1;
        }
    }
    //搜索跳转
    $scope.search=function(){
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});