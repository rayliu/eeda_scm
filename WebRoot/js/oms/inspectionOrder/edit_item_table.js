
$(document).ready(function() {

    var deletedTableIds=[];
    var table_name = 'item_table';
    var addBtn_id = 'add_item';

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
    var itemTable = $("#"+table_name).DataTable({
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
                    return '<input type="text" name="bar_code" value="'+data+'" class="form-control bar_code" />';
                }
            },
            { "data": "CARGO_NAME", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cargo_name" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "CARTON_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="carton_no" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "ITEM_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_code"  value="'+data+'" class="form-control" />';
                }
            },
            { "data": "AMOUNT", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="amount" value="'+data+'" class="form-control" />';
                }
            },
            { "data": "GUARANTEE_DATE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';                    
            		    var field_html = template('table_date_field_template',
		                    {
		                        id: 'GUARANTEE_DATE',
		                        name:'guarantee_date',
		                        value: data.substr(0,19)
		                    }
		                );
	                    return field_html;
                }
            },
            { "data": "SHELVES", "visible":false,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="shelves" value="'+data+'" class="form-control" />';
                }
            }
        ]
    });

    $('#add_item').on('click', function(){
        var item={};
        itemTable.row.add(item).draw(true);
    });
    
    //回车自动添加一行
    $("#"+table_name).on('keyup', '.bar_code', function(e){
    	var self = this;
    	var $this_row = $(self).parent().parent();
        var key = e.which;
        var bar_code = $(self).val();
        var gate_in_order_id = $('#gate_in_order_id').val();
        
        if(bar_code=='' || gate_in_order_id=='')
        	return false;
        //通过bar_code查询出相应的明细表数据
        $.post('/inspectionOrder/getGateInOrderItem',{bar_code:bar_code,gate_in_order_id:gate_in_order_id},function(data){
        	$this_row.find('[name=cargo_name]').val(data.CARGO_NAME);
        	$this_row.find('[name=carton_no]').val(data.CARTON_NO);
        	$this_row.find('[name=item_code]').val(data.ITEM_CODE);
        	$this_row.find('[name=amount]').val(data.PLAN_AMOUNT);
        	$this_row.find('[name=GUARANTEE_DATE]').val(data.SHELF_LIFE);
        	
        	if (key == 13) {
            	$('#'+addBtn_id).click();
            	$this_row.next().find('.bar_code').focus();
            }
        })
        
        
    });
       
    //刷新明细表
    itemOrder.refleshTable = function(order_id){
    	var url = "/inspectionOrder/tableList?order_id="+order_id
        +"&table_type=item";
    	itemTable.ajax.url(url).load();
    }

    
} );