package com.vlbo.acl.mapper

interface GenericMapper<E, D> {

    fun asEntity(dto: D): E

    fun asDTO(entity: E): D

    fun asEntityList(dtoList: List<D>?): List<E>?

    fun asDTOList(entityList: List<E>?): List<D>?

}