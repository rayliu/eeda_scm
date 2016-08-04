
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#item_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        itemTable.row(tr).remove().draw();
    }); 

    inspectionOrder.buildItemDetail=function(){
        var item_table_rows = $("#item_table tr");
        var items_array=[];
        for(var index=0; index<item_table_rows.length; index++){
            if(index==0)
                continue;

            var row = item_table_rows[index];
            var empty = $(row).find('.dataTables_empty').text();
            if(empty)
            	continue;
            
            var id = $(row).attr('id');
            if(!id){
                id='';
            }
            var item={
                id: id,
                bar_code: $(row.children[1]).find('input').val(), 
                item_code: $(row.children[2]).find('input').val(), 
                guarantee_date: $(row.children[3]).find('input').val(), 
                shelves: $(row.children[4]).find('input').val(),
                action: id.length>0?'UPDATE':'CREATE'
            };
            items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            items_array.push(item);
        }
        deletedTableIds = [];
        return items_array;
    };
    
    inspectionOrder.reDrawCargoTable=function(order){
        deletedTableIds=[];
        cargoTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "BAR_CODE": item.BAR_CODE,
                "ITEM_CODE": item.ITEM_CODE,
                "GUARANTEE_DATE": item.GUARANTEE_DATE,
                "SHELVES": item.SHELVES
            };
    
            cargoTable.row.add(item).draw(false);
        }       
    };

    var bindFieldEvent=function(){
    	$('table .date').datetimepicker({  
    	    format: 'yyyy-MM-dd',  
    	    language: 'zh-CN'
    	}).on('changeDate', function(el){
    	    $(".bootstrap-datetimepicker-widget").hide();   
    	    $(el).trigger('keyup');
    	});

    };
    
    //------------事件处理
    var itemTable = $('#item_table').DataTable({
        "processing": true,
        "searching": false,
        "paging": false,
        "info": false,
        "autoWidth": true,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "createdRow": function ( row, data, index ) {
            $(row).attr('id', data.ID);
        }, 
            "drawCallback": function( settings ) {
	        bindFieldEvent();
	    },
        "columns": [
            { "width": "30px",
                "render": function ( data, type, full, meta ) {
                	return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                }
            },
            { "data": "BAR_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control bar_code" required/>';
                }
            },
            { "data": "ITEM_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "GUARANTEE_DATE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';                    
            		    var field_html = template('table_date_field_template',
		                    {
		                        id: 'GUARANTEE_DATE',
		                        value: data.substr(0,19)
		                    }
		                );
	                    return field_html;
                    //return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "SHELVES", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            }
        ]
    });

    $('#add_item').on('click', function(){
        var item={};
        itemTable.row.add(item).draw(true);
    });
    
    //回车自动添加一行
    $("#item_table").on('keydown', '.bar_code', function(e){
        var key = e.which;
        if (key == 13) {
        	$('#add_item').click();
            $(this).parent().parent().next().find('.bar_code').focus();
        }
    });
    
    //刷新明细表
    inspectionOrder.refleshTable = function(order_id){
    	var url = "/inspectionOrder/tableList?order_id="+order_id
        +"&table_type=item";
    	itemTable.ajax.url(url).load();
    }

    
} );