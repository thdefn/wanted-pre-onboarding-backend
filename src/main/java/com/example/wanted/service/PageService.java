package com.example.wanted.service;

import com.example.wanted.domain.cache.PageCache;
import com.example.wanted.domain.repository.PageRepository;
import com.example.wanted.dto.PageDto;
import com.example.wanted.dto.PageForm;
import com.example.wanted.dto.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.example.wanted.domain.cache.PageCache.breadcrumb;

@RequiredArgsConstructor
@Service
public class PageService {
    private final PageRepository pageRepository;

    public void create(long parentId, PageForm form) {
        Long pageId = pageRepository.save(PageVo.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .parentPageId(parentId).build());

        PageCache.addPageBreadCrumb(pageId, parentId);
    }

    public PageDto read(Long pageId) {
        PageVo vo = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("no such data exists"));

        return PageDto.builder()
                .id(vo.getId())
                .title(vo.getTitle())
                .content(vo.getContent())
                .subPages(vo.getSubPages())
                .breadcrumbs(breadcrumb.get(pageId))
                .build();
    }

}
