package md5ac224e58cf5ac4d71ea972bba12b0d46;


public class MapView
	extends android.opengl.GLSurfaceView
	implements
		mono.android.IGCUserPeer,
		android.opengl.GLSurfaceView.Renderer
{
/** @hide */
	public static final String __md_methods;
	static {
		__md_methods = 
			"n_onTouchEvent:(Landroid/view/MotionEvent;)Z:GetOnTouchEvent_Landroid_view_MotionEvent_Handler\n" +
			"n_onDrawFrame:(Ljavax/microedition/khronos/opengles/GL10;)V:GetOnDrawFrame_Ljavax_microedition_khronos_opengles_GL10_Handler:Android.Opengl.GLSurfaceView/IRendererInvoker, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null\n" +
			"n_onSurfaceChanged:(Ljavax/microedition/khronos/opengles/GL10;II)V:GetOnSurfaceChanged_Ljavax_microedition_khronos_opengles_GL10_IIHandler:Android.Opengl.GLSurfaceView/IRendererInvoker, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null\n" +
			"n_onSurfaceCreated:(Ljavax/microedition/khronos/opengles/GL10;Ljavax/microedition/khronos/egl/EGLConfig;)V:GetOnSurfaceCreated_Ljavax_microedition_khronos_opengles_GL10_Ljavax_microedition_khronos_egl_EGLConfig_Handler:Android.Opengl.GLSurfaceView/IRendererInvoker, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=null\n" +
			"";
		mono.android.Runtime.register ("Nutiteq.Ui.MapView, NutiteqMapsSDK.Android, Version=1.0.5613.20114, Culture=neutral, PublicKeyToken=null", MapView.class, __md_methods);
	}


	public MapView (android.content.Context p0) throws java.lang.Throwable
	{
		super (p0);
		if (getClass () == MapView.class)
			mono.android.TypeManager.Activate ("Nutiteq.Ui.MapView, NutiteqMapsSDK.Android, Version=1.0.5613.20114, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065", this, new java.lang.Object[] { p0 });
	}


	public MapView (android.content.Context p0, android.util.AttributeSet p1) throws java.lang.Throwable
	{
		super (p0, p1);
		if (getClass () == MapView.class)
			mono.android.TypeManager.Activate ("Nutiteq.Ui.MapView, NutiteqMapsSDK.Android, Version=1.0.5613.20114, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065:Android.Util.IAttributeSet, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065", this, new java.lang.Object[] { p0, p1 });
	}


	public boolean onTouchEvent (android.view.MotionEvent p0)
	{
		return n_onTouchEvent (p0);
	}

	private native boolean n_onTouchEvent (android.view.MotionEvent p0);


	public void onDrawFrame (javax.microedition.khronos.opengles.GL10 p0)
	{
		n_onDrawFrame (p0);
	}

	private native void n_onDrawFrame (javax.microedition.khronos.opengles.GL10 p0);


	public void onSurfaceChanged (javax.microedition.khronos.opengles.GL10 p0, int p1, int p2)
	{
		n_onSurfaceChanged (p0, p1, p2);
	}

	private native void n_onSurfaceChanged (javax.microedition.khronos.opengles.GL10 p0, int p1, int p2);


	public void onSurfaceCreated (javax.microedition.khronos.opengles.GL10 p0, javax.microedition.khronos.egl.EGLConfig p1)
	{
		n_onSurfaceCreated (p0, p1);
	}

	private native void n_onSurfaceCreated (javax.microedition.khronos.opengles.GL10 p0, javax.microedition.khronos.egl.EGLConfig p1);

	private java.util.ArrayList refList;
	public void monodroidAddReference (java.lang.Object obj)
	{
		if (refList == null)
			refList = new java.util.ArrayList ();
		refList.add (obj);
	}

	public void monodroidClearReferences ()
	{
		if (refList != null)
			refList.clear ();
	}
}
