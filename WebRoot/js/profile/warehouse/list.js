$(document).ready(function() {
	document.title = '订单查询   | '+document.title;
	
    $('#menu_profile').addClass('active').find('ul').addClass('in');

	  //datatable, 动态处理
    var dataTable = $('#eeda-table').DataTable({
    	 "processing": true,
         "searching": false,
         "serverSide": false,
         "scrollX": true,
         "scrollY": "500px",
         "scrollCollapse": true,
         "autoWidth": true,
         "language": {
             "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
         },
         //"ajax": "/damageOrder/list",
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
                	if(Warehouser.isDel){
                		if(full.STATUS != "active"){
                    		str = "启用";
                    		btn = 'btn-success';
                    	}
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
    
    //查询按钮
    $('#searchBtn').click(function(){
        searchData(); 
    })

    var searchData=function(){
    	var warehouse_name = $("#warehouseName_filter").val();
    	var warehouse_address = $("#warehouseAddress_filter").val();
    	
    	var url = "/warehouse/list?warehouse_name="+warehouse_name
    							+"&warehouse_address="+warehouse_address;
    	dataTable.ajax.url(url).load();
    };
} );