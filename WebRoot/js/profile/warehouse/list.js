$(document).ready(function() {
	document.title = '仓库查询   | '+document.title;
	
    $('#menu_profile').addClass('active').find('ul').addClass('in');

	  //datatable, 动态处理
    var dataTable = eeda.dt({
         "id": "eeda-table",
         "ajax": "/warehouse/list",
         "columns": [
            { "data": "WAREHOUSE_NAME", 
            	"render": function ( data, type, full, meta ) {
                    return "<a href='/warehouse/edit?id="+full.ID+"' >"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_ADDRESS"},
            { "data": "CREATE_NAME"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS",
            	"render": function ( data, type, full, meta ) {
            		var str = '停用';
            		var btn = 'btn-danger'
            		if(full.STATUS != "active"){
                		str = "启用";
                		btn = 'btn-success';
                	}
                	
                	return "<button class='btn " + btn + " btn-sm' id='"+full.ID+"'>"+ str + "</button>";
            	}
            }
        ]
    });
    
    //停用按钮
    $('#eeda-table').on('click','.btn',function(){
    	$(this).prop('disabled',true);
    	var id = $(this).attr("id");
    	$.post('/warehouse/stop',{id:id , status:status},function(data){
    		searchData();
    	})
    })
    
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
    	var url = "/warehouse/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
} );