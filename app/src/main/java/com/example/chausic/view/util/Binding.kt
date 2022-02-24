package com.example.chausic.view.util



import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chausic.R
import com.example.chausic.model.data.ChatData
import com.example.chausic.model.data.SendRequest
import com.example.chausic.model.data.UserChatData
import com.example.chausic.view.dashboard.chat.chats.ChatAdapter
import com.example.chausic.view.dashboard.chat.messages.MessagesAdapter
import com.example.chausic.view.dashboard.friends.FriendsAdapter
import com.example.chausic.view.dashboard.requests.RequestAdapter



@BindingAdapter("friendRequest")
fun friendRequest(recyclerView: RecyclerView, data: List<SendRequest>?) {
    val adapter = recyclerView.adapter as RequestAdapter
    if (data != null) {
        adapter.submitList(data)
    }
}
@BindingAdapter("friendList")
fun friendList(recyclerView: RecyclerView, data: List<UserChatData>?) {
    val adapter = recyclerView.adapter as FriendsAdapter
    if (data != null) {
        adapter.submitList(data.toMutableList())
        adapter.notifyDataSetChanged()
    }
}

@BindingAdapter("messageList")
fun messageList(recyclerView: RecyclerView, data: List<ChatData>?) {
    if( data!= null && data.isNotEmpty()){
        val adapter = recyclerView.adapter as MessagesAdapter
        if(adapter.itemCount != 0 ){
            recyclerView.smoothScrollToPosition(adapter.itemCount - 0)
        }
        adapter.submitList(data)
        adapter.notifyDataSetChanged()
    }
}
@BindingAdapter("chatList")
fun chatList(recyclerView: RecyclerView, data: List<UserChatData>?) {
    val adapter = recyclerView.adapter as ChatAdapter
    if (data != null) {
        adapter.submitList(data)
        adapter.notifyDataSetChanged()
    }
}

@BindingAdapter("backGroundPicture")
fun backGroundPicture(img: ImageView, pic: String?){
    if (pic != null && pic.isNotEmpty()){
        Glide
            .with(img.context)
            .load(pic)
            .thumbnail( 0.5f )
            .diskCacheStrategy( DiskCacheStrategy.ALL )
            .error(R.drawable.panda)
            .into(img)
    }else{
        Glide
            .with(img.context)
            .load(R.drawable.panda)
            .into(img)
    }
}
@BindingAdapter("userPicture")
fun userPicture(img: ImageView, pic: String?){
    if (pic != null && pic.isNotEmpty()){
        val imgUri = pic.toUri().buildUpon().scheme("https").build()
        Glide
            .with(img.context)
            .load(imgUri)
            .placeholder(R.drawable.df)
            .circleCrop()
            .error(R.drawable.df)
            .into(img)
    }else{
        Glide
            .with(img.context)
            .load(R.drawable.df)
            .centerCrop()
            .into(img)
    }
}
@BindingAdapter("onlineOffline")
fun onlineOffline(img:ImageButton,status:Boolean){
    if(status){
        img.background = ContextCompat.getDrawable(img.context,R.drawable.custom_online)
    }else{
        img.background = ContextCompat.getDrawable(img.context,R.drawable.custom_offline)
    }
}
@BindingAdapter("seenORNot")
fun seenORNot(img:ImageView,seen:Boolean){
    if(seen){
        img.background = ContextCompat.getDrawable(img.context,R.drawable.seen_s)
    }else{
        img.background = ContextCompat.getDrawable(img.context,R.drawable.received)
    }
}

@BindingAdapter("userText")
fun userText(txt:TextView,data:String?){
    txt.text = data ?: ""

}
@BindingAdapter("textChecker","bol")
fun textChecker(txt:TextView,data:List<UserChatData>?,bol:Boolean){
    if (data == null  && !bol || data.isNullOrEmpty() && !bol){
        txt.visibility = VISIBLE
    }else{
        txt.visibility = GONE
    }
}
@BindingAdapter("textCheckerRequests")
fun textCheckerRequests(txt:TextView,data:List<SendRequest>?){
    if (data == null || data.isNullOrEmpty()){
        txt.visibility = VISIBLE
    }else{
        txt.visibility = GONE
    }
}

@BindingAdapter("online","typing")
fun onlineTyping(txt:TextView,online:Boolean?,typing:Boolean?){
    if(online != null && typing != null){
        if(online && typing){
            txt.visibility = VISIBLE
            txt.text = txt.resources.getString(R.string.typing)
        }else if (online && !typing){
            txt.visibility = VISIBLE
            txt.text = txt.resources.getString(R.string.online)
        }else{
            txt.visibility = GONE
        }
    }
}


