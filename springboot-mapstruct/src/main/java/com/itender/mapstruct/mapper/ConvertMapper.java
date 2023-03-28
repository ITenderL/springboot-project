package com.itender.mapstruct.mapper;

import com.itender.mapstruct.dto.UserDto;
import com.itender.mapstruct.vo.UserVo;
import org.mapstruct.Mapper;

/**
 * @author itender
 * @date 2022/9/22 17:33
 * @desc
 */
@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ConvertMapper {

    /**
     * vo转换成dto
     *
     * @param userVo
     * @return
     */
    UserDto userVoToDto(UserVo userVo);


    /**
     * dto转换vo
     * @param userDto
     * @return
     */
    UserVo userDtoToVo(UserDto userDto);
}
