package de.hsb.greenquest.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import de.hsb.greenquest.ui.viewmodel.ChallengeViewModel
import de.hsb.greenquest.R
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.domain.model.challengeCard
import de.hsb.greenquest.ui.navigation.Screen
import de.hsb.greenquest.ui.theme.OnBackgroundDark
import de.hsb.greenquest.ui.theme.SecondaryVariantDark
import de.hsb.greenquest.ui.theme.spacing
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChallengeScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: ChallengeViewModel = hiltViewModel<ChallengeViewModel>()) {
    val coroutineScope = rememberCoroutineScope()

    val challenges = viewModel.challengeList.collectAsState().value
    val done = challenges.count{ it.done }

    val progress = viewModel.progress.value?.toFloat()
    val requiredCount = viewModel.requiredCount.value?.toFloat()

    val relativeProgress: Float = progress?.let { p ->
        requiredCount?.let {
            r -> p/r
        }
    }?: (-1).toFloat()

    val points = viewModel.points.collectAsState().value

    val imageResource = when (relativeProgress) {
        in 0.0..0.2 -> R.drawable.plant0
        in 0.2..0.4 -> R.drawable.plant1
        in 0.4..0.6 -> R.drawable.plant2
        in 0.6..0.8 -> R.drawable.plant3
        in 0.8..1.1 -> R.drawable.plant4
        else -> R.drawable.plant0
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xff216054))
            .padding(MaterialTheme.spacing.large)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xff67c6c0)), onClick = { /*TODO*/ }) {
                    Text(text = "daily")
                }
                OutlinedButton(colors = ButtonDefaults.buttonColors(contentColor = Color.White), border= BorderStroke(width = 2.dp, color = Color(0xff67c6c0), ), onClick = { navController.navigate(Screen.SearchCardsScreen.route) }) {
                    Text(text = "cards")
                }
            }
            Text(text = "points: $points", color= Color.White)
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                painter = painterResource(imageResource),
                contentDescription = "1"
            )
            Row {
                Text(text = done.toString(), color = OnBackgroundDark)
                Text(text = "/", color = OnBackgroundDark)
                Text(text = challenges.size.toString(), color = Color.White)
            }
        }
        //Spacer(modifier = Modifier.height(16.dp))
        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ){

            if(challenges?.size == 0){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally

                ){
                    Image(
                        modifier = Modifier.clickable { coroutineScope.launch {
                           viewModel.refreshChallenges()
                        } },
                        painter = painterResource(R.drawable.baseline_refresh_24),
                        contentDescription = "1"
                    )
                }
            }

            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
                //verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = true
            ){
                items(challenges.size ?: 0) { index ->
                    ChallengeCard(
                        modifier = Modifier.clickable { coroutineScope.launch {
                            //val challenge = challenges.get(index)
                            //viewModel.updateChallenge(challenge.copy(progress = challenge.progress+1))
                        } },
                        challenge = challenges[index]
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                //item {
                //  Spacer(modifier = Modifier.height(16.dp))
                //}
            }
        }
    }
}

@Composable
fun ChallengeCard(modifier: Modifier = Modifier.background(SecondaryVariantDark), challenge: DailyChallenge) {

    Card(modifier = modifier.fillMaxWidth()){
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = challenge.type,
                fontSize = 20.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.8f)
            )
            if(!challenge.done){
                Box(
                    modifier = Modifier

                        .weight(0.2f),
                    contentAlignment = Alignment.Center
                    //   .background(color = Color.Yellow),
                ){
                    CircularProgressIndicator(
                        progress = challenge.progress.toFloat() / challenge.requiredCount.toFloat() ,
                        //modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = challenge.progress.toString(),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }else{
                Image(
                    painter = painterResource(R.drawable.baseline_check_24),
                    contentDescription = "1",
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}