// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract ClippyToken is ERC20, Ownable {
    struct Question {
        uint256 id;
        address user;
        string content;
    }

    struct Answer {
        uint256 questionId;
        address user;
        string content;
    }

    uint256 private nextQuestionId;
    mapping(uint256 => Question) public questions;
    mapping(uint256 => Answer) public answers;
    mapping(address => bool) public blacklist;
    mapping(address => bool) public hasContributed;

    constructor() ERC20("Clippy", "CY") Ownable(msg.sender) {
        _mint(address(this), 100_000_000 * 10 ** decimals());
    }

    function addQuestion(address user, string calldata content) external onlyOwner {
        require(!blacklist[user], "User is blacklisted");
        questions[nextQuestionId] = Question(nextQuestionId, user, content);
        hasContributed[user] = true;
        nextQuestionId++;
    }

    function addAnswer(address user, uint256 questionId, string calldata content) external onlyOwner {
        require(questions[questionId].id == questionId, "Question does not exist");
        require(!blacklist[user], "User is blacklisted");
        answers[questionId] = Answer(questionId, user, content);
        hasContributed[user] = true;
    }

    function addToBlacklist(address user) external onlyOwner {
        blacklist[user] = true;
    }

    function removeFromBlacklist(address user) external onlyOwner {
        blacklist[user] = false;
    }

    function claimToken() external {
        require(!blacklist[msg.sender], "You are blacklisted");
        require(hasContributed[msg.sender], "No eligible contributions");
        _transfer(address(this), msg.sender, 1 * 10 ** decimals());
    }

    // Query issues by ID
    function getQuestionById(uint256 id) external view returns (Question memory) {
        require(questions[id].id == id, "Question does not exist");
        return questions[id];
    }

    // Query all questions of a user
    function getQuestionsByUser(address user) external view returns (Question[] memory) {
        uint256 count = 0;
        for (uint256 i = 0; i < nextQuestionId; i++) {
            if (questions[i].user == user) {
                count++;
            }
        }
        Question[] memory userQuestions = new Question[](count);
        uint256 index = 0;
        for (uint256 i = 0; i < nextQuestionId; i++) {
            if (questions[i].user == user) {
                userQuestions[index] = questions[i];
                index++;
            }
        }
        return userQuestions;
    }

    // Check whether an address is in the blacklist
    function isBlacklisted(address user) external view returns (bool) {
        return blacklist[user];
    }
}
