package com.github.k.genki0913.verify.repository.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.k.genki0913.verify.repository.constant.View;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ユーザー一覧画面を表示します。
     * <p>
     * リクエストパラメータ {@code keyword} が指定されている場合は、
     * 該当するユーザー名で絞り込み検索を行い、それ以外の場合は全件取得します。
     * </p>
     * * @param keyword 検索キーワード（名前の部分一致）。指定がない場合は {@code null}。
     * 
     * @param model
     *                  ビューにデータを渡すためのモデル。検索結果やキーワードが格納されます。
     * @return ユーザー一覧画面のテンプレート名 ("user/view")
     */
    @GetMapping
    public String view(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("users", userRepository.findByNameContaining(keyword));
        } else {
            model.addAttribute("users", userRepository.findAll());
        }

        return View.VIEW;
    }
}
