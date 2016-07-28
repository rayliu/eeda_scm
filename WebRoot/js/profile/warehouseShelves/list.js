
$(document).ready(function() {
	document.title = '库位查询 | '+document.title;

    $('#menu_profile').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/warehouseShelves/list",
        "columns": [
            { "data": "WAREHOUSE_NAME"},
            { "data": "POSITION", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/warehouseShelves/edit?id="+full.ID+"' >"+data+"</a>";
                }
            },
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}
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
    	var url = "/warehouseShelves/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };

   
} );