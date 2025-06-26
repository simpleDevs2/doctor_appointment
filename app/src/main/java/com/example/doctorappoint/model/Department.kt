package com.example.doctorappoint.model

data class Department(
    val name: String,
    val price: String,
    val description: String? = null
)

val departments = listOf(
    Department("BỆNH LÝ CỘT SỐNG", "150.000đ"),
    Department("CHUYÊN GIA THẦN KINH", "300.000đ", "Chỉ nhận người bệnh tái khám hoặc được giới thiệu khám bởi BS Chuyên khoa"),
    Department("CHĂM SÓC GIẢM NHẸ", "150.000đ", "Chỉ nhận người bệnh tái khám hoặc được giới thiệu khám bởi BS Chuyên khoa"),
    Department("DA LIỄU", "150.000đ", "Chỉ nhận người bệnh từ 3 tuổi"),
    Department("DỊ ỨNG - MIỄN DỊCH LÂM SÀNG", "150.000đ"),
    Department("GHÉP GAN NHI", "150.000đ", "Chỉ nhận người bệnh tái khám hoặc được giới thiệu khám bởi BS Chuyên khoa"),
    Department("HUYẾT HỌC", "150.000đ"),
    Department("Hen-COPD", "150.000đ", "Điều trị về ho, khò khè, khó thở, hen suyễn...")
)