$(document).ready(function() {
	document.title = '订单查询   | '+document.title;
	
    $('#menu_profile').addClass('active').find('ul').addClass('in');

	  //datatable, 动态处理
    var dataTable = $('#eeda-table').DataTable({
    	 "processing": true,
         "searching": false,
         //"serverSide": true,
         "scrollX": true,
         "scrollY": "300px",
         "scrollCollapse": true,
         "autoWidth": false,
         "language": {
             "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
         },
         //"ajax": "/damageOrder/list",
         "columns": [
            { "data": "WAREHOUSE_NAME", 
            	"render": function ( data, type, full, meta ) {
                    return "<a href='/salesOrder/edit?id="+full.ID+"'target='_blank'>"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_ADDRESS"},
            { "data": "CREATE_BY"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS",
            	"render": function ( data, type, full, meta ) {
                	var str="<nobr>";
                	if(Warehouser.isUpdate){
                		str += "<a class='btn  btn-primary btn-sm' href='/warehouse/edit/"+full.ID+"' target='_blank'>"+
	                        "<i class='fa fa-edit fa-fw'></i>"+
	                        "编辑"+
	                        "</a> ";
                	}
                	if(Warehouser.isDel){
                		if(full.STATUS != "inactive"){
                    		str += "<a class='btn btn-danger  btn-sm' href='/warehouse/delete/"+full.ID+"'>"+
    	                            "<i class='fa fa-trash-o fa-fw'></i>"+ 
    	                            "停用"+
    	                        "</a>";
                    	}else{
                    		str += "<a class='btn btn-success  btn-sm' href='/warehouse/delete/"+full.ID+"'>"+
    	                            "<i class='fa fa-trash-o fa-fw'></i>"+ 
    	                            "启用"+
    	                        "</a>";
    	                }
                	}
                	str+="</nobr>";
                	return str;
	                    
	              }
            }
        ]
    });
    
    $('#searchBtn').click(function(){
    	var warehouse_name = $("#warehouseName_filter").val();
    	var warehouse_address = $("#warehouseAddress_filter").val();
    	
    	var url = "/warehouse/list?warehouse_name="+warehouse_name
    							+"&warehouse_address="+warehouse_address;
    	dataTable.ajax.url(url).load();
   });
} );