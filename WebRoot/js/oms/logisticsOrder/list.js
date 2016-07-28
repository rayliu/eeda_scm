
$(document).ready(function() {
	document.title = '运单查询 | '+document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/logisticsOrder/list",
        "columns": [
            { "data": "LOG_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/logisticsOrder/edit?id="+full.ID+"'>"+data+"</a>";
                }
            },
            { "data": "CUSTOM_NAME"},
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS"}
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
    	var url = "/logisticsOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };

});