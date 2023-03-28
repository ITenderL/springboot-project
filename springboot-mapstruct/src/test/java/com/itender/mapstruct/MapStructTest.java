package com.itender.mapstruct;

import com.itender.mapstruct.dto.UserDto;
import com.itender.mapstruct.mapper.ConvertMapper;
import com.itender.mapstruct.vo.UserVo;
import com.itender.mapstruct.vo.Zoo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author itender
 * @date 2022/9/22 17:36
 * @desc
 */
@SpringBootTest
class MapStructTest {

    @Autowired
    private ConvertMapper convertMapper;

    @Test
    void userVo2DtoTest() {
        UserVo userVo = new UserVo().setId(1).setName("itender").setBirthday("2020-12-12 12:12:12").setAddress("深圳").setAge(10);
        System.out.println(convertMapper.userVoToDto(userVo));
    }

    @Test
    void userDtoToVoTest() {
        UserDto userDto = new UserDto().setId(1).setName("itender").setBirthday(new Date()).setAddress("深圳").setAge(10);
        System.out.println(convertMapper.userDtoToVo(userDto));
    }

    @Test
    void OptionalTest() {
        Zoo zoo = new Zoo();
        Zoo.Dog dog = new Zoo.Dog().setAge(11).setName("hello").setType("狗");
        zoo.setDog(null);
        // Optional.ofNullable(zoo).map(Zoo::getDog).map(Zoo.Dog::getAge).filter(a -> Objects.equals(11));
        Optional.ofNullable(zoo.getDog()).filter(d -> d.getAge().equals(11));
    }
}
