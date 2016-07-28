$(document).ready(function() {
    	document.title = '单位查询 | '+document.title;
    	$('#menu_profile').addClass('active').find('ul').addClass('in');

        //datatable, 动态处理
        var dataTable = eeda.dt({
            "id": "eeda-table",
            "ajax": "/unit/list",
            columns:[
   	              { "data": "NAME"}, 
                   {"data": null,"width":"30px", 
                       "render": function ( data, type, full, meta ) {
                         var str = "<a class='btn  btn-primary btn-sm' href='/unit/edit?id="+full.ID+"' >"+
                           "<i class='fa fa-edit fa-fw'></i>"+
                           "编辑"+"</a> ";
                         return str;
                       }
                   },
               ]
        });
      
      $('#resetBtn').click(function(e){
          $("#orderForm")[0].reset();
      });

      $('#searchBtn').click(function(){
          searchData(); 
      })

      buildCondition=function(){
      	var item = {};
      	var orderForm = $('#orderForm input,select');
      	for(var i = 0; i < orderForm.length; i++){
      		var name = orderForm[i].id;
          	var value =orderForm[i].value;
          	if(name){
          		item[name] = value;
          	}
      	}
          return item;
      };

      var searchData=function(){
      	var itemJson = buildCondition();
      	var url = "/unit/list?jsonStr="+JSON.stringify(itemJson);
          dataTable.ajax.url(url).load();
      };
    	
});
