
$(document).ready(function() {
	document.title = '波次单查询   | '+document.title;

	$('#menu_order').addClass('active').find('ul').addClass('in');
	  //datatable, 动态处理
    var dataTable = $('#gate_out_table').DataTable({
        "processing": true,
        "searching": false,
        "serverSide": false,
        "scrollX": true,
        "scrollY": "300px",
        "scrollCollapse": true,
        "autoWidth": false,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "ajax": "/waveOrder/gateOutlist",
        "createdRow": function ( row, data, index ) {
            $(row).attr('id', data.ID);
        },
        "columns": [
            { "data":null,"width":"30px",
                "render": function ( data, type, full, meta ) {
                    return '<input type="checkBox" class ="checkBox">';
                }
            },
            { "data": "ORDER_NO"}, 
            { "data": "CUSTOMER_REFER_NO"}, 
            { "data": "STATUS"},
            { "data": "WAREHOUSE_NAME"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });

    $('#searchBtn').click(function(){
        searchData(); 
    })

   var searchData=function(){
        var order_no = $("#order_no").val(); 
        var status = $('#status').val();
        var start_date = $("#create_stamp_begin_time").val();
        var end_date = $("#create_stamp_end_time").val();
        
        /*  
            查询规则：参数对应DB字段名
            *_no like
            *_id =
            *_status =
            时间字段需成双定义  *_begin_time *_end_time   between
        */
        var url = "/waveOrder/list?order_no="+order_no
             +"&status="+status
             +"&create_stamp_begin_time="+start_date
             +"&create_stamp_end_time="+end_date;

        dataTable.ajax.url(url).load();
    };
    
    //刷新明细表
    waveOrder.refleshTable = function(order_id){
    	var url = "/waveOrder/tableList?order_id="+order_id;
    	cargoTable.ajax.url(url).load();
    }
    
    var hava_check = 0;	
    $('#gate_out_table').on('click','.checkBox',function(){
		var checkbox = $(this).prop('checked');
		if(checkbox){
			++hava_check;	
		}else{
			--hava_check;
		}	
		if(hava_check>0){
			$('#confirmBtn').attr('disabled',false);
		}else{
			$('#confirmBtn').attr('disabled',true);
		}
	});
    
    //全选
    $('#all').on('click',function(){
    	var all = $(this).prop('checked');
    	hava_check = -1;
		$('#gate_out_table input[type="checkbox"]').each(function(){
			if(all){
				$(this).prop('checked',true);
				++hava_check ;
			}else{
	    		$(this).prop('checked',false);
	    		hava_check=0 ;
			}	
    	});
    	
		if(hava_check>0){
			$('#confirmBtn').attr('disabled',false);
		}else{
			$('#confirmBtn').attr('disabled',true);
		}
	});
    
    $('#confirmBtn').on('click',function(){
    	var ids = [];
    	$('#gate_out_table input[type="checkbox"]:checked').each(function(){
    		var id = $(this).parent().parent().attr("id")
    		ids.push(id);
    	});
    	waveOrder.showTable(ids);
    	$('#returnBtn').click();
    });

});