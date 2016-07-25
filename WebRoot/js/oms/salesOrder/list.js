
$(document).ready(function() {
	document.title = '订单查询   | '+document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
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
        "ajax": "/salesOrder/list",
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/salesOrder/edit?id="+full.ID+"'target='_blank'>"+data+"</a>";
                }
            },
            { "data": "CUSTOM_ID"},
            { "data": "PAYER_NAME"}, 
            { "data": "GOODS_VALUE"}, 
            { "data": null,
            	"render": function ( data, type, full, meta ) {
            		var order_cus_status = data.ORDER_CUS_STATUS;
            		var order_ciq_status = data.ORDER_CIQ_STATUS;
            		var pay_status = data.PAY_STATUS;
            		
            		if(order_cus_status=='接收成功' && order_ciq_status=='接收成功' && pay_status=='接收成功'){
            			return '<span style="color:green">订单报关已完成</span>';
            		}else{
            			return '<span style="color:red">订单报关处理中</span>';
            		}
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
        var url = "/salesOrder/list?order_no="+order_no
             +"&status="+status
             +"&create_stamp_begin_time="+start_date
             +"&create_stamp_end_time="+end_date;

        dataTable.ajax.url(url).load();
    };
    
});