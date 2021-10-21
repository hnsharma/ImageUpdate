package com.demo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.demo.ui.theme.ImageDemoTheme
import com.demo.utils.MyCache
import com.demo.utils.PreferenceConnector
import com.demo.utils.Utils
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    imageScreen()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImageDemoTheme {
        imageScreen()
    }
}

@Composable
private fun imageScreen(helloViewModel: HelloViewModel = viewModel()) {
    // helloViewModel follows the Lifecycle as the the Activity or Fragment that calls this
    // composable function.
    val randomX = Random.nextInt(1000, 2500)
    val randomY = Random.nextInt(1000, 2500)
    val urlInit ="https://placekitten.com/$randomX/$randomY"
    // url is the _current_ value of [helloViewModel.url]
    val url: String by helloViewModel.url.observeAsState(urlInit)

    imageUpdate(url = url, onUrlChanged = { helloViewModel.onUrlChanged(it) })
}

@Composable
fun imageUpdate(url: String,
                onUrlChanged: (String) -> Unit) {


    Column(
        // we are using column to align our
        // imageview to center of the screen.
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),

        // below line is used for specifying
        // vertical arrangement.
        verticalArrangement = Arrangement.Center,

        // below line is used for specifying
        // horizontal arrangement.
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // below line is used for creating a variable
        // for our image resource file.
        val img = painterResource(R.drawable.ic_launcher_background)
        // below line is used for creating a modifier for our image
        // which includes image width and image height
        val modifier = Modifier.fillMaxSize()
        // below is the widget for image.
        Card(
            modifier = Modifier
                .size(300.dp)
                .background(Color.DarkGray),
            elevation = 2.dp
        ) {
            SimpleCircularProgressIndicator()
            NetworkImageComponentGlidePreview(url)

        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        imageUpdateButton(onUrlChanged)


    }

}
@Composable
fun imageUpdateButton(onUrlChanged: (String) -> Unit) {
    val context = LocalContext.current
    Button(onClick = {
        val randomX = Random.nextInt(1000, 2500)
        val randomY = Random.nextInt(1000, 2500)
        val url ="https://placekitten.com/$randomX/$randomY"

        if(Utils.isOnline(context = context)) {
            onUrlChanged.invoke(url)
        }
        else
        {
            Utils.showToast(context = context,"Internet not available")
        }
    })
    {
        Text("Update Image")
    }
}


@Composable
fun NetworkImageComponentGlide(
    url: String, modifier: Modifier = Modifier
) {
    // Reacting to state changes is the core behavior of Compose. You will notice a couple new
    // keywords that are compose related - remember & mutableStateOf.remember{} is a helper
    // composable that calculates the value passed to it only during the first composition. It then
    // returns the same value for every subsequent composition. Next, you can think of
    // mutableStateOf as an observable value where updates to this variable will redraw all
    // the composable functions that access it. We don't need to explicitly subscribe at all. Any
    // composable that reads its value will be recomposed any time the value
    // changes. This ensures that only the composables that depend on this will be redraw while the
    // rest remain unchanged. This ensures efficiency and is a performance optimization. It
    // is inspired from existing frameworks like React.
    var imageBitmap : ImageBitmap? = null
    var drawableImage : Drawable? = null
    var image by remember { mutableStateOf(imageBitmap) }
    var drawable by remember { mutableStateOf(drawableImage) }
    val sizeModifier = modifier
        .fillMaxWidth()
        .sizeIn(maxHeight = 200.dp)

    // LocalContext is a LocalComposition for accessting the context value that we are used to using
    // in Android.

    // LocalComposition is an implicit way to pass values down the compose tree. Typically, we pass values
    // down the compose tree by passing them as parameters. This makes it easy to have fairly
    // modular and reusable components that are easy to test as well. However, for certain types
    // of data where multiple components need to use it, it makes sense to have an implicit way
    // to access this data. For such scenarios, we use LocalComposition. In this example, we use the
    // LocalContext to get hold of the Context object. In order to get access to the latest
    // value of the LocalComposition, use the "current" property eg - LocalContext.current. Some other
    // examples of common LocalComposition's are LocalTextInputService,LocalDensity, etc.
    val context = LocalContext.current
    // Sometimes we need to make changes to the state of the app. For those cases, Composes provides
    // some Effect API's which provide a way to perform side effects in a predictable manner.
    // DisposableEffect is one such side effect API that provides a mechanism to perform some
    // clean up actions if the key to the effect changes or if the composable leaves composition.
    var loadUrl = url;
    if(!Utils.isOnline(context = context)) {
        Utils.showToast(context = context,"Internet not available")
        val latUrl =PreferenceConnector.readString(context = context,PreferenceConnector.LAST_IMAGE_URL,null);
        if(latUrl != null)
        {
            loadUrl = latUrl;
        }
        var bitmap = MyCache.retrieveBitmapFromCache(Utils.md5(loadUrl),context = context)
        if(bitmap != null) {
            image = bitmap!!.asImageBitmap()
            val theImage = image
            Column(
                modifier = sizeModifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Image is a pre-defined composable that lays out and draws a given [ImageBitmap].
                if (theImage != null) {
                    Image(bitmap = theImage, contentDescription = null)
                }
            }
        }
    }
   else{
       DisposableEffect(loadUrl) {
           val glide = Glide.with(context)
           val target = object : CustomTarget<Bitmap>() {
               override fun onLoadCleared(placeholder: Drawable?) {
                       image = null
                       drawable = placeholder
               }

               override fun onResourceReady(
                   bitmap: Bitmap,
                   transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
               ) {
                   PreferenceConnector.writeString(context = context,PreferenceConnector.LAST_IMAGE_URL,loadUrl)
                   image = bitmap.asImageBitmap()
                   MyCache.saveBitmapToCahche(Utils.md5(loadUrl),bitmap = bitmap,context = context)
               }
           }
           glide
               .asBitmap()
               .load(url)
               .into(target)

           onDispose {
               image = null
               drawable = null
               glide.clear(target)
           }
       }

       val theImage = image
       val theDrawable = drawable
       if (theImage != null) {
           // Column is a composable that places its children in a vertical sequence. You
           // can think of it similar to a LinearLayout with the vertical orientation.
           // In addition we also pass a few modifiers to it.

           // You can think of Modifiers as implementations of the decorators pattern that are
           // used to modify the composable that its applied to.
           Column(
               modifier = sizeModifier,
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               // Image is a pre-defined composable that lays out and draws a given [ImageBitmap].
               Image(bitmap = theImage, contentDescription = null)
           }
       } else if (theDrawable != null) {
           // We use the Canvas composable that gives you access to a canvas that you can draw
           // into. We also pass it a modifier.
           Canvas(modifier = sizeModifier) {
               drawIntoCanvas { canvas ->
                   theDrawable.draw(canvas.nativeCanvas)
               }
           }
       }
   }


}
@Composable
fun NetworkImageComponentGlidePreview(url: String) {

    NetworkImageComponentGlide(url)
}

@Composable
fun SimpleCircularProgressIndicator(modifier: Modifier = Modifier) {
    val sizeModifier = modifier
        .fillMaxWidth()
        .sizeIn(maxHeight = 40.dp)
    Column(
        modifier = sizeModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        CircularProgressIndicator()
    }
}

class HelloViewModel : ViewModel() {

    // LiveData holds state which is observed by the UI
    // (state flows down from ViewModel)
    val randomX = Random.nextInt(1000, 2500)
    val randomY = Random.nextInt(1000, 2500)
    val urlInit ="https://placekitten.com/$randomX/$randomY"
    private val _url = MutableLiveData(urlInit)
    val url: LiveData<String> = _url

    // onNameChanged is an event we're defining that the UI can invoke
    // (events flow up from UI)
    fun onUrlChanged(newUrl: String) {
        _url.value = newUrl
    }
}