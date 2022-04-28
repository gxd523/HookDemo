package com.gxd.demo.hook

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.gxd.demo.hook.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mainHookBtn.setOnClickListener {// 第一步：明确要hook的对象是OnClickListener
            Toast.makeText(this, "Hello Hook!", Toast.LENGTH_SHORT).show()
        }
        // 第二步：要hook的OnClickListener对象的持有者为ListenerInfo

        hook()
    }

    private fun hook() {
        val getListenerInfoMethod = View::class.java.getDeclaredMethod("getListenerInfo")
        getListenerInfoMethod.isAccessible = true
        val listenerInfo = getListenerInfoMethod.invoke(binding.mainHookBtn)

        val listenerInfoClass = Class.forName("android.view.View\$ListenerInfo")
        val mOnClickListenerField = listenerInfoClass.getDeclaredField("mOnClickListener")
        val onClickListener = mOnClickListenerField.get(listenerInfo) as View.OnClickListener
        // 第四步：将原来的OnClickListener对象替换为我们的代理对象
        mOnClickListenerField.set(listenerInfo, ProxyOnClickListener(onClickListener))
    }

    // 第三步：创建要hook的OnClickListener对象的代理类
    class ProxyOnClickListener(private val listener: View.OnClickListener) : View.OnClickListener by listener {
        override fun onClick(v: View?) {
            Toast.makeText(v?.context, "Before Hook!", Toast.LENGTH_SHORT).show()
            listener.onClick(v)
            Toast.makeText(v?.context, "After Hook!", Toast.LENGTH_SHORT).show()
        }
    }
}