package com.example.doctorappoint.common

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.R
import com.example.doctorappoint.ui.theme.PrimaryColor

@Composable
fun SpacerWidth(width: Dp = 10.dp){
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun SpacerHeight(height : Dp = 10.dp){
    Spacer(modifier = Modifier.height(height))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
   modifier: Modifier = Modifier,
   placeholder: String,
   searchString: String,
   onSearchStringChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchString,
            onValueChange = onSearchStringChange ,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
               Text(
                   text = placeholder,
                   fontSize = 16.sp
               )
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFf4f4f4),
                focusedContainerColor = Color(0xFFf4f4f4),
            ),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                )
            },
            singleLine = true
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BackBtnAndTitle(
    modifier: Modifier = Modifier,
    title: String,
    onBackClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Box(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .align(Alignment.CenterStart)
            .clickable {onBackClick() },
            ){
            Icon(
                painterResource(id = R.drawable.arrow),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart),
                tint = PrimaryColor
            )
        }

        Text(
            text = title,
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.W600, color = PrimaryColor),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

    @Composable
    fun BackButton(onClick: () -> Unit = {} ){
        Box(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
        ){
            IconButton (onClick =onClick) {
                Icon(painter = painterResource(id = R.drawable.arrowleft),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(20.dp).padding(start = 24.dp)
                )
            }
        }
    }

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(PrimaryColor),
        enabled = enabled
    ){
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(text = text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.W500)
        }
    }

}

fun String.removeVietnameseAccents(): String {
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val temp = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
    return regex.replace(temp, "")
}

/**
 * Chuyển đổi định dạng ngày từ yyyy-MM-dd thành dd/MM/yyyy
 * @param dateString Ngày theo định dạng yyyy-MM-dd
 * @return Ngày theo định dạng dd/MM/yyyy hoặc chuỗi gốc nếu có lỗi
 */
fun formatDateForAPI(dateString: String?): String {
    return if (dateString.isNullOrBlank()) {
        ""
    } else {
        try {
            val inputDate = java.time.LocalDate.parse(dateString)
            inputDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        } catch (e: Exception) {
            Log.e("DateUtils", "Error formatting date: ${e.message}")
            dateString // Fallback to original format if parsing fails
        }
    }
}

/**
 * Chuyển đổi định dạng ngày từ dd/MM/yyyy thành yyyy-MM-dd
 * @param dateString Ngày theo định dạng dd/MM/yyyy
 * @return Ngày theo định dạng yyyy-MM-dd hoặc chuỗi gốc nếu có lỗi
 */
fun formatDateFromAPI(dateString: String?): String {
    return if (dateString.isNullOrBlank()) {
        ""
    } else {
        try {
            val inputDate = java.time.LocalDate.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            inputDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            Log.e("DateUtils", "Error formatting date: ${e.message}")
            dateString // Fallback to original format if parsing fails
        }
    }
}