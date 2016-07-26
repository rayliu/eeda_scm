
$(document).ready(function() {

	var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 

    waveOrder.buildItemDetail=function(){
        var cargo_table_rows = $("#cargo_table tr");
        var cargo_items_array=[];
        for(var index=0; index<cargo_table_rows.length; index++){
            if(index==0)
                continue;

            var row = cargo_table_rows[index];
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
            cargo_items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            cargo_items_array.push(item);
        }
        deletedTableIds = [];
        return cargo_items_array;
    };
    
  
    //------------事件处理
    var cargoTable = $('#cargo_table').DataTable({
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
                	return meta.row+1;
                }
            },
            { "data": "ORDER_NO_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="order_no_code" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "SALES_ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="sales_order_no" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "ITEM_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_code" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "AMOUNT", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='1';
                    return '<input type="number" name="amount" value="'+data+'" class="form-control easyui-numberbox" data-options="max:0"/>';
                }
            },
            { "data": "SHELVES", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="shelves" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CARGO_BAR_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cargo_bar_code" value="'+data+'" class="form-control" />';
                }
            } 
        ]
    });

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(true);
    });
    
    //刷新明细表
    waveOrder.refleshTable = function(order_id){
    	var url = "/waveOrder/tableList?order_id="+order_id;
    	cargoTable.ajax.url(url).load();
    }
    
    waveOrder.showTable = function(order_ids){
    	var url = "/waveOrder/tableList?order_ids="+order_ids;
    	cargoTable.ajax.url(url).load();
    }
} );