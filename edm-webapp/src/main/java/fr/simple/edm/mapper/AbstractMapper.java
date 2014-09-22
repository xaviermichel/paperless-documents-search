package fr.simple.edm.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class AbstractMapper<T, S> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMapper.class);
    
	private Class<T> tt;
	private Class<S> ss;
	
	public AbstractMapper(Class<T> model, Class<S> dto) {
		tt = model;
		ss = dto;
	}
	
	public T dtoToBo(S dto) {
		T t = null;
		try {
			t = tt.newInstance();
			BeanUtils.copyProperties(dto, t);
		} catch (Exception e) {
			logger.error("Failed to convert dto to bo", e);
		}
		return t;
	}
	
	public S boToDto(T bo) {
		S s = null;
		try {
			s = ss.newInstance();
			BeanUtils.copyProperties(bo, s);
		} catch (Exception e) {
		    logger.error("Failed to convert bo to dto", e);
		}
		return s;
	}

	/**
	 * Try to map the DTO to BO, returns null if failed
	 */
	@SuppressWarnings("unchecked")
    public <U> T dtoToBoOrNull(U potentialDto) {
	    try {
    	    if (! potentialDto.getClass().equals(ss.newInstance().getClass())) {
    	        return null;
    	    }
    	    return dtoToBo((S) potentialDto);
	    }
	    catch(Exception e) {
	        return null;
	    }
	}
	
	/**
     * Try to map the BO to DTO, returns null if failed
     */
    @SuppressWarnings("unchecked")
    public <U> S boToDtoOrNull(U potentialBo) {
        try {
            if (! potentialBo.getClass().equals(tt.newInstance().getClass())) {
                return null;
            }
            return boToDto((T) potentialBo);
        }
        catch(Exception e) {
            return null;
        }
    }
	
	public List<T> dtoToBo(List<S> dtos) {
        List<T> bos = new ArrayList<>(dtos.size());
        for (S dto : dtos) {
            bos.add(dtoToBo(dto));
        }
        return bos;
	}
	
	public List<S> boToDto(List<T> bos) {
        List<S> dtos = new ArrayList<>(bos.size());
        for (T bo : bos) {
            dtos.add(boToDto(bo));
        }
        return dtos;
	}
}
