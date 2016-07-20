
$(document).ready(function() {
	document.title = '库位查询 | '+document.title;

    $('#menu_profile').addClass('active').find('ul').addClass('in');
    
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
        //"ajax": "/damageOrder/list",
        "columns": [
            { "data": "WAREHOUSE_NAME"},
            { "data": "POSITION", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/warehouseShelves/edit?id="+full.ID+"'target='_blank'>"+data+"</a>";
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
        var warehouse_id = $("#warehouse_id").val();
        var start_date = $("#create_stamp_begin_time").val();
        var end_date = $("#create_stamp_end_time").val();
        
        /*  
            查询规则：参数对应DB字段名
            *_no like
            *_id =
            *_status =
            时间字段需成双定义  *_begin_time *_end_time   between
        */
        var url = "/warehouseShelves/list?warehouse_id="+warehouse_id
             +"&create_stamp_begin_time="+start_date
             +"&create_stamp_end_time="+end_date;
        dataTable.ajax.url(url).load();
    };
    

} );