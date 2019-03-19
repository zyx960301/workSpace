		//品牌服务
		app.service("brandService",function($http){
			//查询品牌列表
			this.findAll=function(){
				return $http.get('../brand/findAll.do');
			}
			//分页
			this.findPage=function(page,size){
				return $http.get('../brand/findPage.do?page='+page +'&size='+size);
			}
			//增加
			this.add=function(entity){
				return $http.post('../brand/add.do',entity);
			}
			//更新
			this.update=function(entity){
				return $http.post('../brand/update.do',entity);
			}
			//查询实体
			this.findOne=function(id){
				return $http.get('../brand/findOne.do?id='+id);
			}
			//删除
			this.dele=function(Ids){
				return $http.get('../brand/delete.do?ids='+Ids);
			}
			//搜索
			this.search=function(page,size,searchEntity){
				return $http.post('../brand/search.do?page='+page +'&size='+size, searchEntity);
			}
			//品牌列表
			this.selectOptionList=function(){
				return $http.get('../brand/selectOptionList.do');
			}
		});