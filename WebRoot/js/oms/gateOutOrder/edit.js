
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        
        $(this).attr('disabled', true);

        var items_array = gateOutOrder.buildCargoDetail();
        
        var order = {
            id: $('#order_id').val(),
            order_no: $('#order_no').val(),
            warehouse_id: $('#warehouse_id').val(),  
            consignor: $('#consignor').val(),  
            order_type: $('#order_type').val(),  
            gate_out_date: $('#gate_out_date').val(),
            goods_type: $('#goods_type').val(),  
            remark: $('#remark').val(),  
            status: $('#status').val()==''?'暂存':$('#status').val(), 
            item_list: items_array
        };

        //异步向后台提交数据
        $.post('/gateOutOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(data.CREATE_BY_NAME);
            	if(order_id=='')
            		$("#create_stamp").val(eeda.getDate());
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
				$('#status').val(order.STATUS);
				contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                $('#confirmBtn').attr('disabled', false);
                $('#canselBtn').attr('disabled', false);
                
                //异步刷新字表
                gateOutOrder.refleshTable(order.ID);
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });  
    
    //确认按钮
    $('#confirmBtn').click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/gateOutOrder/confirmOrder', {params:order_id}, function(data){
    		if(data.ID){
    			$('#status').val(data.STATUS);
    			$('#saveBtn').attr('disabled', true);
    			$.scojs_message('确认成功', $.scojs_message.TYPE_OK);
    		}else{
    			$.scojs_message('确认失败', $.scojs_message.TYPE_ERROR);
    			self.attr('disabled',false);
    		}
    	})
    })
    
    //取消按钮
    $('#canselBtn').click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/gateOutOrder/canselOrder', {params:order_id}, function(data){
    		if(data.ID){
    			$('#status').val(data.STATUS);
    			$('#saveBtn').attr('disabled', true);
    			$('#confirmBtn').attr('disabled', true);
    			$.scojs_message('取消成功', $.scojs_message.TYPE_OK);
    		}else{
    			$.scojs_message('取消失败', $.scojs_message.TYPE_ERROR);
    			self.attr('disabled',false);
    		}
    	})
    })
    
    //按钮控制
    var order_id = $("#order_id").val();
    var status = $("#status").val();
    if(order_id != ''){
    	if(status=='暂存'){
    		$('#saveBtn').attr('disabled', false);
    		$('#confirmBtn').attr('disabled', false);
    		$('#canselBtn').attr('disabled', false);
    	}else if(status=='已确认'){
    		$('#canselBtn').attr('disabled', false);
    	}
    }else{
    	$('#saveBtn').attr('disabled', false);
    }
} );