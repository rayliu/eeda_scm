
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;
	var confirmBtn = 'confirmBtn';

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    var buidOrder = function(){
    	var order = {};
    	var orderForm = $('#orderForm input,textarea');
    	order['id'] = $('#order_id').val();
    	for(var i = 0; i < orderForm.length; i++){
    		var name = orderForm[i].id;
        	var value =orderForm[i].value;
        	if(name){
        		if(name=='status' && value=='')
        			value = '暂存';
        		order[name]=value;
        	}
    	}
        return order;
	}
    
    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        $(this).attr('disabled', true);

        var items_array = itemOrder.buildItemDetail();
        var order = {};
        order = buidOrder();
        order['item_list'] = items_array;
        
        //异步向后台提交数据
        $.post('/moveOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#creator_name").val(order.CREATOR_NAME);
				$('#status').val(order.STATUS);

				eeda.contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                $('#'+confirmBtn).attr('disabled', false);
                $('#cancelBtn').attr('disabled', false);
                
                //异步刷新字表
                itemOrder.refleshTable(order.ID);
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
    $('#'+confirmBtn).click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/moveOrder/confirmOrder', {params:order_id}, function(data){
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
    $('#cancelBtn').click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/gateInOrder/cancelOrder', {params:order_id}, function(data){
    		if(data.ID){
    			$('#status').val(data.STATUS);
    			$('#'+confirmBtn).attr('disabled', true);
    			$('#checkBtn').attr('disabled', true);
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
    		$('#'+confirmBtn).attr('disabled', false);
    		$('#cancelBtn').attr('disabled', false);
    	}else if(status=='已确认'){
    		$('#cancelBtn').attr('disabled', false);
    	}
    }else{
    	$('#saveBtn').attr('disabled', false);
    }
} );