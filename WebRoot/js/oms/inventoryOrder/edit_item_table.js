
$(document).ready(function() {

    var deletedTableIds=[];
    var table_name = 'cargo_table';
    var addBtn_id = 'add_cargo';

    //删除一行
    $("#"+table_name).on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        itemTable.row(tr).remove().draw();
    }); 

    itemOrder.buildItemDetail=function(){
        var item_table_rows = $("#"+table_name+" tr");
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
            
            var item={}
            item.id = id;
            for(var i = 1; i < row.childNodes.length; i++){
            	var name = $(row.childNodes[i]).find('input').attr('name');
            	var value = $(row.childNodes[i]).find('input').val();
            	if(name){
            		item[name] = value;
            	}
            }
            item.action = id.length > 0?'UPDATE':'CREATE';
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
    
    //------------事件处理
    var itemTable = $('#'+table_name+'').DataTable({
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
        "columns": [
            { "width": "30px",
                "render": function ( data, type, full, meta ) {
                	return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                }
            },
            { "data": null, 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return meta.row+1;
                }
            },
            { "data": "CARGO_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cargo_code" value="'+data+'" class="form-control" />';
                }
            }, 
            { "data": "CARGO_NAME", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cargo_name" value="'+data+'" class="form-control" />';
                }
            }, 
            { "data": "UNIT", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="unit" value="'+data+'" class="form-control" />';
                }
            }, 
            { "data": "SHELVES", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="shelves" value="'+data+'" class="form-control" />';
                }
            }, 
            { "data": "AMOUNT", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="amount" value="'+data+'" class="form-control" />';
                }
            }, 
            { "data": "CHECK_AMOUNT", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="check_amount" value="'+data+'" class="form-control" />';
                }
            },
        ]
    });

    $('#'+addBtn_id).on('click', function(){
        var item={};
        itemTable.row.add(item).draw(true);
    });
    
    
    //刷新明细表
    itemOrder.refleshTable = function(order_id){
    	var url = "/inventoryOrder/tableList?order_id="+order_id
        +"&table_type=item";
    	itemTable.ajax.url(url).load();
    }

    
} );