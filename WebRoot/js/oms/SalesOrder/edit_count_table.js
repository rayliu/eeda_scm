
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#count_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'));
        
        countTable.row(tr).remove().draw();
    }); 

    salesOrder.buildCountDetail=function(){
        var count_table_rows = $("#count_table tr");
        var count_items_array=[];
        for(var index=0; index<count_table_rows.length; index++){
            if(index==0)
                continue;

            var row = count_table_rows[index];
            var id = $(row).attr('id');
            if(!id){
                id='';
            }

            var item={
                id: id,
                name: $(row.children[1]).find('input').val(), 
                type: $(row.children[2]).find('select').val(), 
                amount: $(row.children[3]).find('input').val(), 
                remark: $(row.children[4]).find('input').val(), 
                action: $('#order_id').val().length>0?'UPDATE':'CREATE'
            };
            count_items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            count_items_array.push(item);
        }
        return count_items_array;
    };

    salesOrder.reDrawCountTable=function(order){
        deletedTableIds=[];
        countTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "NAME": item.NAME,
                "TYPE": item.TYPE,
                "AMOUNT": item.AMOUNT,
                "REMARK": item.REMARK
            };
    
            countTable.row.add(item).draw(false);
        }       
    };

    //------------事件处理
    var countTable = $('#count_table').DataTable({
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
                	if($('#status').val()!='已结案')
                		return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                	else
                		return '';
                }
            },
            { "data": "NAME", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "TYPE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<select class="form-control search-control"'
		                    + '<option  value ="charge">应收</option>'
		                    + '<option  value ="cost">应付</option>'
		                    + '</select>';
                }
            },
            { "data": "AMOUNT" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            }, 
            { "data": "REMARK" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" value="'+data+'" class="form-control" required/>';
                }
            }
        ]
    });

    $('#add_count').on('click', function(){
        var item={};
        countTable.row.add(item).draw(true);
    });

    
} );