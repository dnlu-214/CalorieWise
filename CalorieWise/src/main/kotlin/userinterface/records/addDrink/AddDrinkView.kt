package userinterface.records.addDrink

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


enum class AddDrinkEvent {
    FirstNameEvent,
    LastNameEvent,
    UppercaseEvent,
    LowercaseEvent,
    ResetEvent
}


@Composable
fun DrinkEntry(name: String, quantity: String) {
    Card(
        modifier = Modifier.width(360.dp),
        backgroundColor = Color.White
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.border(1.dp, Color.LightGray),
        ) {
            Text(
                text = "$name $quantity",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f).padding(5.dp)
            )
            Button(
                onClick = {
                },
                shape = CircleShape,
                modifier = Modifier.padding(5.dp)
            ) {
                Text("+")
            }
        }
    }

}

@Composable
fun AddDrinkView() {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource("icons/DrinkIcon.png"),
            contentDescription = "Drink Page Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            item {
                Spacer(modifier = Modifier.height(25.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { },
                        modifier = Modifier.width(118.dp)
                    ) {
                        Text("New")
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.width(118.dp)
                    ) {
                        Text("Favourite")
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.width(118.dp)
                    ) {
                        Text("Recent")
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))

                DrinkEntry("Water", "200 ml")
                DrinkEntry("Water", "200 ml")
            }
        }

    }
}