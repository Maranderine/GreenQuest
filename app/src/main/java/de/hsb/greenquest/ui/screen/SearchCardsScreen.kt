package de.hsb.greenquest.ui.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import de.hsb.greenquest.R
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.theme.spacing
import de.hsb.greenquest.ui.viewmodel.PortfolioViewModel
import de.hsb.greenquest.ui.viewmodel.SearchCardsViewModel
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog

@Composable
fun SearchCardsScreen(navController: NavController) {
    val portfolioViewModel = hiltViewModel<SearchCardsViewModel>()
    val cards = portfolioViewModel.challengeCards.collectAsState().value
    val currIdx = portfolioViewModel.cardsIdx.collectAsState().value
    val loading: Boolean = portfolioViewModel.loading.collectAsState().value


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xff216054))
            .padding(MaterialTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            OutlinedButton(colors = ButtonDefaults.buttonColors(contentColor = Color.White), border= BorderStroke(width = 2.dp, color = Color(0xff67c6c0), ), onClick = { navController.navigate(Screen.ChallengeScreen.route) }) {
                Text(text = "daily")
            }
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xff67c6c0)), onClick = { /*TODO*/ }) {
                Text(text = "cards")
            }

        }
        val sizeC = cards.size
        Text(text = "streak: 3", color= Color.White)
        Spacer(modifier = Modifier.size(40.dp))
        Box(modifier = Modifier.height(300.dp)){
            if(loading){
                Text(text = "Loading...", color = Color.White)
            }else{
                if(currIdx == cards.size){
                    Image(
                        modifier = Modifier.size(300.dp),
                        painter  = painterResource(R.drawable.emptycard),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "1"
                    )
                }else{
                    Image(
                        modifier = Modifier.size(300.dp),
                        painter = rememberAsyncImagePainter(cards[currIdx].imgPath),
                        //painter  = painterResource(R.drawable.baseline_check_24),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "1"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Box(

                modifier = Modifier,
                contentAlignment = Alignment.TopCenter,
            ) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    translate(top = -15.dp.toPx()) {
                        drawCircle(Color.White, radius = 20.dp.toPx())
                    }
                }
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { portfolioViewModel.changeIdx(currIdx - 1) },
                    painter = painterResource(R.drawable.baseline_arrow_left_24),
                    contentDescription = "1",
                    alpha = if(currIdx == 0) 0.2F else 1.0F
                )
            }
            if(currIdx == cards.size) {
                Box(

                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        drawCircle(Color.White, radius = 30.dp.toPx(),)
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { portfolioViewModel.loadChallengeCard() },
                        painter = painterResource(R.drawable.baseline_refresh_24),
                        contentDescription = "1"
                    )
                }
                Box(

                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        drawCircle(Color.White, radius = 30.dp.toPx(),)
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp),
                        alpha = 0.2F,
                        painter = painterResource(R.drawable.baseline_help_outline_24),
                        contentDescription = "1",

                    )
                }
            }else{
                Box(

                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        drawCircle(Color.White, radius = 30.dp.toPx(),)
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { portfolioViewModel.deleteCard() },
                        painter = painterResource(R.drawable.baseline_clear_24),
                        contentDescription = "1"
                    )
                }
                Box(

                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        drawCircle(Color.White, radius = 30.dp.toPx(),)
                    }
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                portfolioViewModel.toggleDialog();
                                portfolioViewModel.setDialogText("hellooo")
                            },
                        painter = painterResource(R.drawable.baseline_help_outline_24),
                        contentDescription = "1"
                    )
                }
            }
            Box(

                modifier = Modifier,
                contentAlignment = Alignment.TopCenter,
            ) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    translate(top = -15.dp.toPx()) {
                        drawCircle(Color.White, radius = 20.dp.toPx())
                    }
                }
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { portfolioViewModel.changeIdx(currIdx + 1) },
                    painter = painterResource(R.drawable.baseline_arrow_right_24),
                    contentDescription = "1",
                    alpha = if(currIdx == cards.size) 0.2F else 1.0F
                )
            }
        }

        Spacer(modifier = Modifier.size(40.dp))

        val idx = if( currIdx >= cards.size) "-" else (currIdx+1).toString()
        Text(text = "$idx / $sizeC", color= Color.White)


        val openDialog = portfolioViewModel.openDialog.collectAsState()
        val dialogText = portfolioViewModel.DialogText.collectAsState()

        if (openDialog.value) {

            Dialog(onDismissRequest = { portfolioViewModel.toggleDialog() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = "This is a minimal dialog",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            /*AlertDialog(
                onDismissRequest = { portfolioViewModel.toggleDialog() },
                onConfirmation = {
                    portfolioViewModel.toggleDialog()
                    println("Confirmation registered") // Add logic here to handle confirmation.
                },
                dialogTitle = "Hint",
                dialogText = dialogText.value,
                icon = Icons.Default.Info
            )*/
        }
    }
}